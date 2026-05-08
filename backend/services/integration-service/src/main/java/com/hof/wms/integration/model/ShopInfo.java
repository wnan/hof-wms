package com.hof.wms.integration.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 店铺信息，同时用于 API 反序列化和数据库持久化
 */
@Data
@TableName(value = "shop_info", schema = "sf_api")
public class ShopInfo {

    @TableId(type = IdType.INPUT)
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("sellerId")
    private String sellerId;

    @SerializedName("region")
    private String region;

    @SerializedName("marketplaceId")
    private String marketplaceId;

    @SerializedName("adStatus")
    private String adStatus;

    @SerializedName("status")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
