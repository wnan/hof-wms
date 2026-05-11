package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 广告组合列表响应数据（游标分页）
 */
public class PortfolioListResponse {

    @SerializedName("nextToken")
    private String nextToken;

    @SerializedName("itemList")
    private List<PortfolioInfo> itemList;

    public String getNextToken() { return nextToken; }
    public void setNextToken(String nextToken) { this.nextToken = nextToken; }
    public List<PortfolioInfo> getItemList() { return itemList; }
    public void setItemList(List<PortfolioInfo> itemList) { this.itemList = itemList; }
}
