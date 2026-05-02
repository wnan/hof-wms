import { request, type PageParams, type PageResult } from "./request";
import type { SkuItem, SkuCategory } from "@/types/sku";

export const skuApi = {
  list: (params: PageParams) => request.get<PageResult<SkuItem>>("/sku/list", params),
  detail: (id: string) => request.get<SkuItem>(`/sku/${id}`),
  create: (data: Partial<SkuItem>) => request.post<SkuItem>("/sku", data),
  update: (id: string, data: Partial<SkuItem>) => request.put<SkuItem>(`/sku/${id}`, data),
  remove: (id: string) => request.del<void>(`/sku/${id}`),
  toggleStatus: (id: string, status: "on" | "off") =>
    request.post<void>(`/sku/${id}/status`, { status }),
  batchRemove: (ids: string[]) => request.post<void>("/sku/batch-remove", { ids }),
  importSkus: (data: unknown) => request.post<{ success: number; fail: number }>("/sku/import", data),
};

export const skuCategoryApi = {
  tree: () => request.get<SkuCategory[]>("/sku/category/tree"),
  create: (data: Partial<SkuCategory>) => request.post<SkuCategory>("/sku/category", data),
  update: (id: string, data: Partial<SkuCategory>) =>
    request.put<SkuCategory>(`/sku/category/${id}`, data),
  remove: (id: string) => request.del<void>(`/sku/category/${id}`),
};
