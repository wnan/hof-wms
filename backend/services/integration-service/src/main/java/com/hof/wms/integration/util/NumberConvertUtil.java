package com.hof.wms.integration.util;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 数字转换工具类
 * 将Excel中的字符串格式数字转换为BigDecimal
 * 处理百分比(如 "15.2%")、货币符号(如 "$1,234.56")、千分位等
 */
@Slf4j
public class NumberConvertUtil {

    public static BigDecimal toBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        String cleaned = value.trim();

        if (cleaned.endsWith("%")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
        }

        cleaned = cleaned.replace("$", "").replace("€", "").replace("£", "").replace("¥", "");
        cleaned = cleaned.replace(",", "");
        cleaned = cleaned.trim();

        if (cleaned.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (cleaned.startsWith("(") && cleaned.endsWith(")")) {
            cleaned = "-" + cleaned.substring(1, cleaned.length() - 1);
        }

        try {
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("无法将 '{}' 转换为数字，原始值: '{}'", cleaned, value);
            return BigDecimal.ZERO;
        }
    }

    public static Long toLong(String value) {
        BigDecimal decimal = toBigDecimal(value);
        return decimal.longValue();
    }

    public static BigDecimal parsePercentage(String value, boolean isPercentage) {
        BigDecimal result = toBigDecimal(value);
        if (isPercentage) {
            return result.divide(BigDecimal.valueOf(100), 6, java.math.RoundingMode.HALF_UP);
        }
        return result;
    }
}
