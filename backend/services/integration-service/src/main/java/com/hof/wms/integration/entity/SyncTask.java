package com.hof.wms.integration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hof.wms.common.db.handler.JsonbStringTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "sync_task", schema = "integration", autoResultMap = true)
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

    /** 任务参数(JSONB)，值支持SpEL表达式 */
    @TableField(value = "params", typeHandler = JsonbStringTypeHandler.class)
    private String params;

    /** 参数类全限定名，用于将params JSON反序列化为具体对象 */
    @TableField("params_class")
    private String paramsClass;

    /** 是否启用 */
    @TableField("enabled")
    private Boolean enabled;

    @TableField("last_execute_status")
    private String lastExecuteStatus;

    @TableField("last_execute_message")
    private String lastExecuteMessage;

    private String description;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /** 同步类型常量：SellFox店铺导入 */
    public static final String TYPE_SHOP_INFO = "SHOP_INFO";
    /** 同步类型常量：SellFox广告活动导入 */
    public static final String TYPE_AD_CAMPAIGN = "AD_CAMPAIGN";
    /** 同步类型常量：SellFox广告组合导入 */
    public static final String TYPE_PORTFOLIO = "PORTFOLIO";
}
