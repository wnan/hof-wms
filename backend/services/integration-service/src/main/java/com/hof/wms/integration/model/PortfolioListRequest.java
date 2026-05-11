package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

/**
 * 广告组合列表请求体
 */
public class PortfolioListRequest {

    @SerializedName("shopId")
    private String shopId;

    @SerializedName("nextToken")
    private String nextToken;

    @SerializedName("pageSize")
    private String pageSize;

    @SerializedName("portfolioId")
    private String portfolioId;

    public PortfolioListRequest() {
    }

    public PortfolioListRequest(String shopId, String nextToken, String pageSize) {
        this.shopId = shopId;
        this.nextToken = nextToken;
        this.pageSize = pageSize;
    }

    public String getShopId() { return shopId; }
    public void setShopId(String shopId) { this.shopId = shopId; }
    public String getNextToken() { return nextToken; }
    public void setNextToken(String nextToken) { this.nextToken = nextToken; }
    public String getPageSize() { return pageSize; }
    public void setPageSize(String pageSize) { this.pageSize = pageSize; }
    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String portfolioId) { this.portfolioId = portfolioId; }
}
