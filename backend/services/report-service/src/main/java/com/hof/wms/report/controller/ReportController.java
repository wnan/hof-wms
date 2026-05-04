package com.hof.wms.report.controller;

import com.hof.wms.common.result.ApiResult;
import com.hof.wms.report.entity.AIAnalysisRecord;
import com.hof.wms.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/{type}")
    public ApiResult<Map<String, Object>> detail(@PathVariable String type) {
        Map<String, Object> data = reportService.getReportDetail(type);
        return ApiResult.success(Map.of(
                "type", type,
                "summary", data.get("data")
        ));
    }

    @GetMapping("/{type}/export")
    public ApiResult<Map<String, Object>> export(@PathVariable String type) {
        Map<String, Object> result = reportService.exportReport(type);
        return ApiResult.success(result);
    }

    @PostMapping("/ai-analyze")
    public ApiResult<Map<String, Object>> aiAnalyze(@RequestBody Map<String, Object> command) {
        AIAnalysisRecord record = reportService.analyze(command);
        return ApiResult.success(Map.of(
                "answer", record.getResultText(),
                "id", record.getId(),
                "chart", java.util.List.of(
                        Map.of("name", "第1周", "value", 850),
                        Map.of("name", "第2周", "value", 920),
                        Map.of("name", "第3周", "value", 880),
                        Map.of("name", "第4周", "value", 1020)
                )
        ));
    }
}
