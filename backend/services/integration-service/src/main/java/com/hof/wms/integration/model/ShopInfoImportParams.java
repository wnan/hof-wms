package com.hof.wms.integration.model;

import lombok.Data;

/**
 * 店铺信息导入任务参数
 */
@Data
public class ShopInfoImportParams {

    /** 每页条数 */
    private String pageSize = "200";
}
