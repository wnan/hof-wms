import { request, type PageParams, type PageResult } from "./request";
import type { InventoryItem, StockCheck, StockAlert } from "@/types/inventory";

export const inventoryApi = {
  list: (params: PageParams) => request.get<PageResult<InventoryItem>>("/inventory/list", params),
  checkSave: (data: Partial<StockCheck>) => request.post<StockCheck>("/inventory/check/save", data),
  checkSubmit: (id: string) => request.post<void>(`/inventory/check/${id}/submit`),
  alertList: (params: PageParams) => request.get<PageResult<StockAlert>>("/inventory/alert", params),
  alertSet: (skuId: string, threshold: number) =>
    request.post<void>("/inventory/alert/set", { skuId, threshold }),
};
