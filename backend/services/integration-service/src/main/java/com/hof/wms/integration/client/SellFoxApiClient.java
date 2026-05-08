package com.hof.wms.integration.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hof.wms.integration.config.SellFoxConfig;
import com.hof.wms.integration.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellFoxApiClient {

    private final SellFoxConfig config;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new com.google.gson.JsonSerializer<LocalDateTime>() {
                @Override
                public com.google.gson.JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
                    return new com.google.gson.JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new com.google.gson.JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();
    private static final MediaType JSON_MEDIA = MediaType.parse("application/json; charset=utf-8");

    /** 提前刷新Token的缓冲时间（秒），避免临界过期 */
    private static final long TOKEN_REFRESH_BUFFER_SECONDS = 300;

    // ======================== Token 管理 ========================

    private String accessToken;
    private long tokenExpireAtMillis;

    /**
     * 确保Access Token有效，过期则自动刷新
     */
    public synchronized void ensureAccessToken() throws IOException {
        if (isTokenValid()) {
            return;
        }
        log.info("Access Token 已过期或为空，正在刷新...");
        refreshToken();
    }

    /**
     * 判断当前Token是否有效
     */
    private boolean isTokenValid() {
        return this.accessToken != null && !this.accessToken.isEmpty()
                && System.currentTimeMillis() < this.tokenExpireAtMillis;
    }

    /**
     * 获取 Access Token
     */
    public synchronized ApiResponse<AccessTokenResponse> getAccessToken() throws IOException {
        HttpUrl url = HttpUrl.parse(config.getBaseUrl() + "/api/oauth/v2/token.json").newBuilder()
                .addQueryParameter("client_id", config.getClientId())
                .addQueryParameter("client_secret", config.getClientSecret())
                .addQueryParameter("grant_type", "client_credentials")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                ApiResponse<AccessTokenResponse> apiResponse = GSON.fromJson(responseBody,
                        new TypeToken<ApiResponse<AccessTokenResponse>>() {}.getType());

                if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                    AccessTokenResponse tokenData = apiResponse.getData();
                    this.accessToken = tokenData.getAccessToken();
                    long expiresIn = tokenData.getExpiresIn() > 0 ? tokenData.getExpiresIn() : 86400;
                    this.tokenExpireAtMillis = System.currentTimeMillis()
                            + (expiresIn - TOKEN_REFRESH_BUFFER_SECONDS) * 1000;
                    log.info("成功获取 Access Token，有效期 {} 秒，将在 {} 秒后自动刷新",
                            expiresIn, expiresIn - TOKEN_REFRESH_BUFFER_SECONDS);
                } else {
                    log.error("获取 Access Token 失败: {}", apiResponse.getMsg());
                }
                return apiResponse;
            } else {
                log.error("获取 Access Token 请求失败: {} {}", response.code(), response.message());
            }
        }
        return null;
    }

    /**
     * 重新获取Token
     */
    public synchronized void refreshToken() throws IOException {
        this.accessToken = null;
        this.tokenExpireAtMillis = 0;
        getAccessToken();
    }

    // ======================== 接口秒级限流 ========================

    /** 记录每个API路径的最后调用时间戳（毫秒），同一client_id同一接口1秒内不能重复调用 */
    private final ConcurrentHashMap<String, Long> lastCallTimeMap = new ConcurrentHashMap<>();

    /**
     * 等待直到可以调用指定API（同一client_id同一接口1秒内只能调用一次）
     */
    private void waitIfNeeded(String apiPath) {
        long now = System.currentTimeMillis();
        Long lastCallTime = lastCallTimeMap.get(apiPath);
        if (lastCallTime != null) {
            long elapsed = now - lastCallTime;
            if (elapsed < 1000) {
                long sleepMs = 1000 - elapsed;
                log.debug("接口 {} 调用间隔不足1秒，等待 {} ms", apiPath, sleepMs);
                try {
                    Thread.sleep(sleepMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        lastCallTimeMap.put(apiPath, System.currentTimeMillis());
    }

    // ======================== 签名 ========================

    /**
     * HmacSHA256签名
     */
    public static String hmacsha256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secret_key);
        return Hex.encodeHexString(hmac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * 生成签名
     */
    public String generateSign(String requestUrl, String method, String timestamp, String nonce) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("url", requestUrl);
        params.put("method", method.toLowerCase());
        params.put("access_token", this.accessToken);
        params.put("client_id", config.getClientId());
        params.put("timestamp", timestamp);
        params.put("nonce", nonce);

        String data = params.entrySet().stream()
                .filter(e -> Objects.nonNull(e.getValue()))
                .map(e -> e.getKey() + "=" + e.getValue())
                .sorted()
                .collect(Collectors.joining("&"));

        return hmacsha256(config.getClientSecret(), data);
    }

    // ======================== 业务API ========================

    /**
     * 获取店铺列表
     */
    public ApiResponse<ShopListResponse> getShopList(String pageNo, String pageSize) throws IOException {
        String apiPath = "/api/shop/pageList.json";
        String requestBody = GSON.toJson(new ShopListRequest(pageNo, pageSize));
        return callApi(apiPath, "POST", requestBody, ShopListResponse.class);
    }

    /**
     * 创建报表任务
     */
    public ApiResponse<CreateTaskResponse> createTask(CreateTaskRequest request) throws IOException {
        String apiPath = "/api/cpc/download/createTask.json";
        String requestBody = GSON.toJson(request);
        return callApi(apiPath, "POST", requestBody, CreateTaskResponse.class);
    }

    /**
     * 查询任务进度
     */
    public ApiResponse<TaskListResponse> queryTaskList(TaskQueryRequest request) throws IOException {
        String apiPath = "/api/cpc/download/pageList.json";
        String requestBody = GSON.toJson(request);
        return callApi(apiPath, "POST", requestBody, TaskListResponse.class);
    }

    /**
     * 下载文件到本地
     */
    public void downloadFile(String fileUrl, String savePath) throws IOException {
        Request request = new Request.Builder()
                .url(fileUrl)
                .get()
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                java.nio.file.Files.write(
                        java.nio.file.Paths.get(savePath),
                        response.body().bytes()
                );
                log.info("文件下载成功: {}", savePath);
            } else {
                throw new IOException("文件下载失败: " + response.code() + " " + response.message());
            }
        }
    }

    /**
     * 通用API调用方法
     * 自动处理：1) Token过期刷新  2) 同接口秒级限流
     */
    public <T> ApiResponse<T> callApi(String apiPath, String method, String requestBodyJson, Class<T> responseClass) throws IOException {
        // 1. 确保Token有效
        ensureAccessToken();

        // 2. 同接口秒级限流
        waitIfNeeded(apiPath);

        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String nonce = String.valueOf((int) (Math.random() * 1000000));
            String sign = generateSign(apiPath, method, timestamp, nonce);

            HttpUrl url = HttpUrl.parse(config.getBaseUrl() + apiPath).newBuilder()
                    .addQueryParameter("access_token", this.accessToken)
                    .addQueryParameter("client_id", config.getClientId())
                    .addQueryParameter("timestamp", timestamp)
                    .addQueryParameter("nonce", nonce)
                    .addQueryParameter("sign", sign)
                    .build();

            RequestBody body = RequestBody.create(requestBodyJson, JSON_MEDIA);

            Request request = new Request.Builder()
                    .url(url)
                    .method(method, body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log.debug("API调用成功: {}", responseBody);

                    // 检查返回中是否有token失效的标识，若有则刷新重试
                    ApiResponse<T> apiResponse = GSON.fromJson(responseBody,
                            TypeToken.getParameterized(ApiResponse.class, responseClass).getType());
                    if (apiResponse != null && isTokenExpiredResponse(apiResponse)) {
                        log.warn("API返回Token失效标识，刷新Token后重试: {}", apiPath);
                        refreshToken();
                        return callApi(apiPath, method, requestBodyJson, responseClass);
                    }
                    return apiResponse;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "N/A";
                    log.error("API调用失败: {} {}, Body: {}", response.code(), response.message(), errorBody);

                    // 401/403 可能是Token过期，刷新重试
                    if (response.code() == 401 || response.code() == 403) {
                        log.warn("API返回 {} 状态码，刷新Token后重试: {}", response.code(), apiPath);
                        refreshToken();
                        return callApi(apiPath, method, requestBodyJson, responseClass);
                    }
                }
            }
        } catch (Exception e) {
            log.error("API调用异常: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断API响应是否表示Token失效
     */
    private boolean isTokenExpiredResponse(ApiResponse<?> apiResponse) {
        // 常见的Token失效错误码
        int code = apiResponse.getCode();
        return code == 401 || code == 403 || code == 40001 || code == 40014 || code == 42001;
    }
}
