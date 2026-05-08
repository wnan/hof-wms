package com.hof.wms.integration.model;

import lombok.Data;

/**
 * 广告活动数据导入任务参数
 * 日期字段支持SpEL表达式，如 T(java.time.LocalDate).now().minusDays(1).toString()
 */
@Data
public class AdCampaignImportParams {

    /** 广告类型编码，默认 sp */
    private String adTypeCode = "sp";

    /** 报告类型编码，默认 adCampaignReport */
    private String reportTypeCode = "adCampaignReport";

    /** 时间粒度，默认 daily */
    private String timeUnit = "daily";

    /** 报告开始日期，支持SpEL表达式 */
    private String startDateExpr;

    /** 报告结束日期，支持SpEL表达式 */
    private String endDateExpr;

    /** 删除数据日期，支持SpEL表达式 */
    private String deleteDateExpr;
}
