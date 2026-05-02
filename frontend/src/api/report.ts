import { request } from "./request";

export interface ReportSeries { name: string; value: number; }
export interface ReportData {
  summary: { label: string; value: number; delta?: number }[];
  series: ReportSeries[];
  table: Record<string, unknown>[];
}

export const reportApi = {
  detail: (type: string, params: Record<string, unknown>) =>
    request.get<ReportData>(`/report/${type}`, params),
  exportFile: (type: string, format: "excel" | "pdf", params: Record<string, unknown>) =>
    request.get<{ url: string }>(`/report/${type}/export`, { format, ...params }),
  aiAnalyze: (prompt: string) =>
    request.post<{ answer: string; chart?: ReportSeries[] }>("/report/ai-analyze", { prompt }),
};
