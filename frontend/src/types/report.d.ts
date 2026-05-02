export interface ReportFilter {
  startDate?: string;
  endDate?: string;
  warehouse?: string;
  category?: string;
}

export interface ReportCardMeta {
  type: string;
  title: string;
  description: string;
  category: "inbound" | "outbound" | "inventory" | "comprehensive";
}
