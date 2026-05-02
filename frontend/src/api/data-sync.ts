import { request, type PageParams, type PageResult } from "./request";
import type { SyncTask, SyncLog } from "@/types/data-sync";

export const dataSyncApi = {
  list: (params: PageParams) => request.get<PageResult<SyncTask>>("/data-sync/list", params),
  detail: (id: string) => request.get<SyncTask>(`/data-sync/${id}`),
  save: (data: Partial<SyncTask>) => request.post<SyncTask>("/data-sync/save", data),
  test: (id: string) => request.post<{ ok: boolean; message: string }>(`/data-sync/${id}/test`),
  execute: (id: string) => request.post<void>(`/data-sync/${id}/execute`),
  logs: (params: PageParams) => request.get<PageResult<SyncLog>>("/data-sync/logs", params),
};
