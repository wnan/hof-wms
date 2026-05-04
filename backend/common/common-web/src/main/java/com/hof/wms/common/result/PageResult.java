package com.hof.wms.common.result;

import java.util.List;

public record PageResult<T>(List<T> records, Long total, Long current, Long size) {
}
