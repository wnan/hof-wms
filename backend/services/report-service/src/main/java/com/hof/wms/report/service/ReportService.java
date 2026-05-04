package com.hof.wms.report.service;

import com.hof.wms.report.entity.AIAnalysisRecord;
import com.hof.wms.report.entity.ReportTask;
import com.hof.wms.report.repository.AIAnalysisRepository;
import com.hof.wms.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final AIAnalysisRepository aiAnalysisRepository;

    public Map<String, Object> getReportDetail(String type) {
        return Map.of(
                "type", type,
                "data", Map.of(
                        "totalInbound", 1280,
                        "totalOutbound", 1010,
                        "inventoryValue", 2560000
                )
        );
    }

    public Map<String, Object> exportReport(String type) {
        return Map.of("filePath", "/downloads/" + type + ".xlsx");
    }

    @Transactional
    public AIAnalysisRecord analyze(Map<String, Object> params) {
        AIAnalysisRecord record = new AIAnalysisRecord();
        record.setAnalysisType((String) params.get("analysisType"));
        record.setInputParams(params);
        record.setResultText("基于历史数据分析，建议关注库存周转率优化，补货周期建议调整为7天一次。");
        record.setResultJson(Map.of("confidence", 0.92, "trend", "up"));
        return aiAnalysisRepository.save(record);
    }
}
