package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 任务信息
 */
public class TaskInfo {

    @SerializedName("id")
    private String id;

    @SerializedName("taskName")
    private String taskName;

    @SerializedName("shopId")
    private List<String> shopId;

    @SerializedName("adTypeCode")
    private String adTypeCode;

    @SerializedName("reportTypeCode")
    private String reportTypeCode;

    @SerializedName("reportState")
    private String reportState;

    @SerializedName("downloadUrl")
    private List<String> downloadUrl;

    @SerializedName("createTime")
    private String createTime;

    @SerializedName("reportCycle")
    private String reportCycle;

    @SerializedName("timeUnit")
    private String timeUnit;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public List<String> getShopId() { return shopId; }
    public void setShopId(List<String> shopId) { this.shopId = shopId; }
    public String getAdTypeCode() { return adTypeCode; }
    public void setAdTypeCode(String adTypeCode) { this.adTypeCode = adTypeCode; }
    public String getReportTypeCode() { return reportTypeCode; }
    public void setReportTypeCode(String reportTypeCode) { this.reportTypeCode = reportTypeCode; }
    public String getReportState() { return reportState; }
    public void setReportState(String reportState) { this.reportState = reportState; }
    public List<String> getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(List<String> downloadUrl) { this.downloadUrl = downloadUrl; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getReportCycle() { return reportCycle; }
    public void setReportCycle(String reportCycle) { this.reportCycle = reportCycle; }
    public String getTimeUnit() { return timeUnit; }
    public void setTimeUnit(String timeUnit) { this.timeUnit = timeUnit; }
}
