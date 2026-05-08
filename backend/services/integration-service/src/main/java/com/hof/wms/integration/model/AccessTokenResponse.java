package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

/**
 * Access Token响应数据类
 */
public class AccessTokenResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("expires_in")
    private long expiresIn;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    @Override
    public String toString() {
        return "AccessTokenResponse{accessToken='" + accessToken + "', expiresIn=" + expiresIn + "}";
    }
}
