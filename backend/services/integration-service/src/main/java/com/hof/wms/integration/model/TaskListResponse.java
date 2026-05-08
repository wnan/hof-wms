package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * 任务列表响应数据
 */
public class TaskListResponse {

    @SerializedName("pageNo")
    private int pageNo;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPage")
    private int totalPage;

    @SerializedName("totalSize")
    private int totalSize;

    @SerializedName("rows")
    private List<TaskInfo> rows;

    public int getPageNo() { return pageNo; }
    public void setPageNo(int pageNo) { this.pageNo = pageNo; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getTotalPage() { return totalPage; }
    public void setTotalPage(int totalPage) { this.totalPage = totalPage; }
    public int getTotalSize() { return totalSize; }
    public void setTotalSize(int totalSize) { this.totalSize = totalSize; }
    public List<TaskInfo> getRows() { return rows; }
    public void setRows(List<TaskInfo> rows) { this.rows = rows; }
}
