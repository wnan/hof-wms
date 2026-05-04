package com.hof.wms.integration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "sync_task", schema = "integration")
public class SyncTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_name")
    private String taskName;

    @TableField("system_name")
    private String systemName;

    @TableField("api_url")
    private String apiUrl;

    @TableField("auth_type")
    private String authType;

    @TableField("sync_type")
    private String syncType;

    @TableField("trigger_type")
    private String triggerType;

    @TableField("cron_expr")
    private String cronExpr;

    private String status;

    @TableField("last_sync_time")
    private LocalDateTime lastSyncTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
