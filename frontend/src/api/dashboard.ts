import { request } from "./request";
import type { InboundOrder } from "@/types/inbound";

export interface DashboardSummary {
  metrics: {
    inboundCount: number;
    outboundCount: number;
    inventorySkuCount: number;
    alertCount: number;
  };
  latestInbound: InboundOrder[];
  trend: { date: string; inbound: number; outbound: number }[];
  categoryDist: { name: string; value: number }[];
  warehouseLoad: { name: string; value: number }[];
}

export const dashboardApi = {
  summary: () => request.get<DashboardSummary>("/dashboard/summary"),
};
