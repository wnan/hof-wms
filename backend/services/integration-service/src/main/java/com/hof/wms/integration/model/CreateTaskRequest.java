package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 创建报表任务请求体
 */
public class CreateTaskRequest {

    @SerializedName("shopIds")
    private List<String> shopIds;

    @SerializedName("adTypeCode")
    private String adTypeCode;

    @SerializedName("reportTypeCode")
    private String reportTypeCode;

    @SerializedName("timeUnit")
    private String timeUnit;

    @SerializedName("reportStartDate")
    private String reportStartDate;

    @SerializedName("reportEndDate")
    private String reportEndDate;

    public List<String> getShopIds() { return shopIds; }
    public void setShopIds(List<String> shopIds) { this.shopIds = shopIds; }
    public String getAdTypeCode() { return adTypeCode; }
    public void setAdTypeCode(String adTypeCode) { this.adTypeCode = adTypeCode; }
    public String getReportTypeCode() { return reportTypeCode; }
    public void setReportTypeCode(String reportTypeCode) { this.reportTypeCode = reportTypeCode; }
    public String getTimeUnit() { return timeUnit; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
    public String getReportStartDate() { return reportStartDate; }
    public void setReportStartDate(String reportStartDate) { this.reportStartDate = reportStartDate; }
    public String getReportEndDate() { return reportEndDate; }
    public void setReportEndDate(String reportEndDate) { this.reportEndDate = reportEndDate; }
}
