package com.hof.wms.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "report_task", schema = "report")
public class ReportTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("report_type")
    private String reportType;

    @TableField("query_params")
    private String queryParams;

    @TableField("generate_status")
    private String generateStatus;

    @TableField("file_path")
    private String filePath;

    @TableField("created_by")
    private Long createdBy;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
