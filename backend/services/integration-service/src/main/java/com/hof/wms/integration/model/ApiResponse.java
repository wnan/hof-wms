package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

/**
 * 通用API响应类
 */
public class ApiResponse<T> {

    @SerializedName("code")
    private int code;

    @SerializedName("msg")
    private String msg;

    @SerializedName("data")
    private T data;

    @SerializedName("ts")
    private long ts;

    @SerializedName("requestId")
    private String requestId;

    public ApiResponse() {
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public long getTs() { return ts; }
    public void setTs(long ts) { this.ts = ts; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public boolean isSuccess() {
        return code == 0;
    }

    @Override
    public String toString() {
        return "ApiResponse{code=" + code + ", msg='" + msg + "', data=" + data +
                ", ts=" + ts + ", requestId='" + requestId + "'}";
    }
}
