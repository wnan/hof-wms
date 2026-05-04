package com.hof.wms.integration.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "sync_field_mapping", schema = "integration")
public class SyncFieldMapping {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private Long taskId;

    @TableField("source_field")
    private String sourceField;

    @TableField("target_field")
    private String targetField;

    @TableField("convert_rule")
    private String convertRule;
}
