package com.hof.wms.integration.model;

import com.google.gson.annotations.SerializedName;

/**
 * 创建报表任务响应数据
 */
public class CreateTaskResponse {

    @SerializedName("id")
    private String id;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @Override
    public String toString() {
        return "CreateTaskResponse{id='" + id + "'}";
    }
}
