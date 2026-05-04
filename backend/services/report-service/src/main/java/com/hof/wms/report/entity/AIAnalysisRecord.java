package com.hof.wms.report.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hof.wms.common.db.handler.JacksonJsonTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@TableName(value = "ai_analysis_record", schema = "report", autoResultMap = true)
public class AIAnalysisRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("analysis_type")
    private String analysisType;

    @TableField(value = "input_params", typeHandler = JacksonJsonTypeHandler.class)
    private Map<String, Object> inputParams;

    @TableField("result_text")
    private String resultText;

    @TableField(value = "result_json", typeHandler = JacksonJsonTypeHandler.class)
    private Map<String, Object> resultJson;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
