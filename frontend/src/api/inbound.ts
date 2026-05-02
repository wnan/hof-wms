import { request, type PageParams, type PageResult } from "./request";
import type { InboundOrder } from "@/types/inbound";

export const inboundApi = {
  list: (params: PageParams) => request.get<PageResult<InboundOrder>>("/inbound/list", params),
  detail: (id: string) => request.get<InboundOrder>(`/inbound/${id}`),
  save: (data: Partial<InboundOrder>) => request.post<InboundOrder>("/inbound/save", data),
  submit: (id: string) => request.post<void>(`/inbound/${id}/submit`),
  approve: (id: string, pass: boolean) => request.post<void>(`/inbound/${id}/approve`, { pass }),
  confirm: (id: string) => request.post<void>(`/inbound/${id}/confirm`),
  remove: (id: string) => request.del<void>(`/inbound/${id}`),
};
