package com.hof.wms.integration.model;

import lombok.Data;

/**
 * 广告组合(Portfolio)导入任务参数
 */
@Data
public class PortfolioImportParams {

    /** 每页条数，默认100，支持100~1000 */
    private String pageSize = "100";
}
