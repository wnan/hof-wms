package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 查询广告任务进度请求
 */
public class TaskQueryRequest {

    @SerializedName("taskIds")
    private List<String> taskIds;

    @SerializedName("taskName")
    private String taskName;

    @SerializedName("adTypeCodeList")
    private List<String> adTypeCodeList;

    @SerializedName("reportTypeCodeList")
    private List<String> reportTypeCodeList;

    @SerializedName("createTimeStart")
    private String createTimeStart;

    @SerializedName("createTimeEnd")
    private String createTimeEnd;

    @SerializedName("shopIds")
    private List<String> shopIds;

    @SerializedName("pageNo")
    private int pageNo;

    @SerializedName("pageSize")
    private int pageSize;

    public List<String> getTaskIds() { return taskIds; }
    public void setTaskIds(List<String> taskIds) { this.taskIds = taskIds; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public List<String> getAdTypeCodeList() { return adTypeCodeList; }
    public void setAdTypeCodeList(List<String> adTypeCodeList) { this.adTypeCodeList = adTypeCodeList; }
    public List<String> getReportTypeCodeList() { return reportTypeCodeList; }
    public void setReportTypeCodeList(List<String> reportTypeCodeList) { this.reportTypeCodeList = reportTypeCodeList; }
    public String getCreateTimeStart() { return createTimeStart; }
    public void setCreateTimeStart(String createTimeStart) { this.createTimeStart = createTimeStart; }
    public String getCreateTimeEnd() { return createTimeEnd; }
    public void setCreateTimeEnd(String createTimeEnd) { this.createTimeEnd = createTimeEnd; }
    public List<String> getShopIds() { return shopIds; }
    public void setShopIds(List<String> shopIds) { this.shopIds = shopIds; }
    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
}
