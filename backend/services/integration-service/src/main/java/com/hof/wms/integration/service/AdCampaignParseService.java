package com.hof.wms.integration.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.hof.wms.integration.entity.AdCampaignReport;
import com.hof.wms.integration.model.dto.AdCampaignReportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 广告活动报告Excel解析服务
 * 使用EasyExcel将Excel映射到DTO，再从DTO转换到AdCampaignReport
 */
@Slf4j
@Service
public class AdCampaignParseService {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<AdCampaignReport> parseExcel(String filePath, String shopId, String reportTypeCode) {
        log.info("开始解析Excel文件: {}", filePath);

        List<AdCampaignReport> reports = new ArrayList<>();

        EasyExcel.read(filePath, AdCampaignReportDto.class,
                new PageReadListener<AdCampaignReportDto>(dataList -> {
                    for (AdCampaignReportDto dto : dataList) {
                        AdCampaignReport report = convertToEntity(dto, shopId, reportTypeCode);
                        if (report != null && report.getReportDate() != null && report.getCampaignName() != null) {
                            reports.add(report);
                        }
                    }
                })).sheet().doRead();

        log.info("Excel解析完成，共 {} 条数据", reports.size());
        return reports;
    }

    private AdCampaignReport convertToEntity(AdCampaignReportDto dto, String defaultShopId, String reportTypeCode) {
        if (dto == null) return null;

        AdCampaignReport report = new AdCampaignReport();
        report.setShopId(defaultShopId);
        report.setReportTypeCode(reportTypeCode);
        report.setShopName(dto.getShopName());
        report.setReportDate(parseDate(dto.getDate()));
        report.setCampaignName(dto.getCampaignName());
        report.setTargetingType(dto.getTargetingType());
        report.setCampaignStatus(dto.getCampaignStatus());
        report.setPortfolioId(dto.getPortfolioId());
        report.setCampaignId(dto.getCampaignId());
        report.setCampaignStartDate(parseDate(dto.getCampaignStartDate()));
        report.setCampaignEndDate(parseEndDate(dto.getCampaignEndDate()));

        // 整数字段
        report.setImpressions(parseLong(dto.getImpressions()));
        report.setClicks(parseLong(dto.getClicks()));
        report.setAdOrders(parseLong(dto.getAdOrders()));
        report.setAdvertisedProductOrders(parseLong(dto.getAdvertisedProductOrders()));
        report.setOtherProductAdOrders(parseLong(dto.getOtherProductAdOrders()));
        report.setAdUnits(parseLong(dto.getAdUnits()));
        report.setAdvertisedProductUnits(parseLong(dto.getAdvertisedProductUnits()));
        report.setOtherProductAdUnits(parseLong(dto.getOtherProductAdUnits()));

        // 金额字段：提取数值（可能含$符号和千分位）
        report.setSpend(parseCurrency(dto.getSpend()));
        report.setCpc(parseCurrency(dto.getCpc()));
        report.setAdSales(parseCurrency(dto.getAdSales()));
        report.setAdvertisedProductSales(parseCurrency(dto.getAdvertisedProductSales()));
        report.setOtherProductAdSales(parseCurrency(dto.getOtherProductAdSales()));

        // 百分比字段：1.87% → 0.0187
        report.setCtr(parsePercentage(dto.getCtr()));
        report.setConversionRate(parsePercentage(dto.getConversionRate()));
        report.setAcos(parsePercentage(dto.getAcos()));

        // ROAS 普通数值
        report.setRoas(parseBigDecimal(dto.getRoas()));

        return report;
    }

    /**
     * 解析日期字符串，支持多种格式及Excel数值日期
     */
    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        value = value.trim();

        // 尝试标准格式
        try {
            return LocalDate.parse(value, DATE_FMT);
        } catch (Exception ignored) {
        }

        // 尝试其他常见格式
        String[] patterns = {"yyyy/MM/dd", "yyyy/M/d", "MM/dd/yyyy", "M/d/yyyy"};
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
            } catch (Exception ignored) {
            }
        }

        // 尝试Excel数值日期（如 46076）
        try {
            double numericDate = Double.parseDouble(value);
            return LocalDate.of(1899, 12, 30).plusDays((long) numericDate);
        } catch (Exception ignored) {
        }

        log.warn("无法解析日期: {}", value);
        return null;
    }

    /**
     * 解析广告活动结束时间，处理"无结束日期"等特殊值
     */
    private LocalDate parseEndDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        if ("无结束日期".equals(value.trim())) return null;
        return parseDate(value);
    }

    /**
     * 解析金额字符串，去除$符号和千分位逗号
     * 例如: "$1,234.56" → 1234.56, "0.56" → 0.56
     */
    private BigDecimal parseCurrency(String value) {
        BigDecimal result = parseBigDecimal(value);
        // 如果值已经是不含%的纯数字（EasyExcel读取数值型单元格时），直接返回
        return result;
    }

    /**
     * 解析百分比字符串
     * "1.87%" → 0.0187, "0.0187"(已是小数) → 0.0187
     */
    private BigDecimal parsePercentage(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        String cleaned = value.trim();

        if (cleaned.endsWith("%")) {
            // "1.87%" → 去掉%号，除以100
            cleaned = cleaned.substring(0, cleaned.length() - 1).trim();
            return parseBigDecimal(cleaned).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        }

        // 可能已经是小数形式（EasyExcel读取数值型单元格时）
        BigDecimal decimal = parseBigDecimal(cleaned);
        // 判断：如果值 > 1，很可能是百分比形式（如 1.87 而非 0.0187）
        // 但Excel中数值型单元格的百分比已经是小数形式，所以直接返回
        return decimal;
    }

    /**
     * 解析通用BigDecimal，去除货币符号、千分位等
     * 支持: "[$US$]1,234.56" → 1234.56, "$1,234.56" → 1234.56
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return BigDecimal.ZERO;
        String cleaned = value.trim();
        // 去除方括号货币标记，如 [US$]、[EUR]、[CNY]
        cleaned = cleaned.replaceAll("\\[.*?\\]", "");
        cleaned = cleaned.replace("$", "").replace("€", "").replace("£", "").replace("¥", "");
        cleaned = cleaned.replace(",", "");
        cleaned = cleaned.trim();

        if (cleaned.startsWith("(") && cleaned.endsWith(")")) {
            cleaned = "-" + cleaned.substring(1, cleaned.length() - 1);
        }

        if (cleaned.isEmpty()) return BigDecimal.ZERO;

        try {
            return new BigDecimal(cleaned).setScale(6, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            log.warn("无法将 '{}' 转换为数字", value);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 解析Long值
     */
    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty()) return 0L;
        try {
            return Long.parseLong(value.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            try {
                return new BigDecimal(value.trim().replace(",", "")).longValue();
            } catch (Exception ex) {
                log.warn("无法将 '{}' 转换为Long", value);
                return 0L;
            }
        }
    }
}
