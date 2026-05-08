package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

/**
 * 店铺列表请求体
 */
public class ShopListRequest {

    @SerializedName("pageNo")
    private String pageNo;

    @SerializedName("pageSize")
    private String pageSize;

    public ShopListRequest() {
    }

    public ShopListRequest(String pageNo, String pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public String getPageNo() { return pageNo; }
    public void setPageNo(String pageNo) { this.pageNo = pageNo; }
    public String getPageSize() { return pageSize; }
    public void setPageSize(String pageSize) { this.pageSize = pageSize; }
}
