package com.hof.wms.integration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 广告组合(Portfolio)信息，同时用于 API 反序列化和数据库持久化
 */
@Data
@TableName(value = "portfolio_info", schema = "sf_api")
public class PortfolioInfo {

    @TableId(type = IdType.INPUT)
    @SerializedName("portfolioId")
    @TableField("portfolio_id")
    private String portfolioId;

    @SerializedName("shopId")
    @TableField("shop_id")
    private String shopId;

    @SerializedName("name")
    private String name;

    @SerializedName("servingStatus")
    @TableField("serving_status")
    private String servingStatus;

    @SerializedName("inBudget")
    @TableField("in_budget")
    private String inBudget;

    @SerializedName("amount")
    private String amount;

    @SerializedName("policy")
    private String policy;

    @SerializedName("startDate")
    @TableField("start_date")
    private String startDate;

    @SerializedName("endDate")
    @TableField("end_date")
    private String endDate;

    @SerializedName("creationDate")
    @TableField("creation_date")
    private String creationDate;

    @SerializedName("lastUpdatedDate")
    @TableField("last_updated_date")
    private String lastUpdatedDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
