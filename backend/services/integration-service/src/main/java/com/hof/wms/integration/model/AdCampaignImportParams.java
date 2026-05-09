package com.hof.wms.integration.model;

import lombok.Data;

import java.util.List;

/**
 * 广告活动数据导入任务参数
 * 日期字段支持SpEL表达式，如 T(java.time.LocalDate).now().minusDays(1).toString()
 */
@Data
public class AdCampaignImportParams {

    /** 广告类型编码，默认 sp */
    private String adTypeCode = "sp";

    /** 报告类型编码，默认 adCampaignReport（保留，向后兼容） */
    private String reportTypeCode = "adCampaignReport";

    /** 批量报告类型数组，优先于 reportTypeCode */
    private List<String> reportTypeCodes;

    /** 时间粒度，默认 daily */
    private String timeUnit = "daily";

    /** 报告开始日期，支持SpEL表达式 */
    private String startDateExpr;

    /** 报告结束日期，支持SpEL表达式 */
    private String endDateExpr;

    /**
     * 获取有效的报告类型列表
     * 优先使用 reportTypeCodes，回退到 reportTypeCode
     */
    public List<String> getEffectiveReportTypeCodes() {
        if (reportTypeCodes != null && !reportTypeCodes.isEmpty()) {
            return reportTypeCodes;
        }
        if (reportTypeCode != null && !reportTypeCode.isEmpty()) {
            return List.of(reportTypeCode);
        }
        return List.of();
    }
}
