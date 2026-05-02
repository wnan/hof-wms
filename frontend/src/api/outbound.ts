import { request, type PageParams, type PageResult } from "./request";
import type { OutboundOrder } from "@/types/outbound";

export const outboundApi = {
  list: (params: PageParams) => request.get<PageResult<OutboundOrder>>("/outbound/list", params),
  detail: (id: string) => request.get<OutboundOrder>(`/outbound/${id}`),
  save: (data: Partial<OutboundOrder>) => request.post<OutboundOrder>("/outbound/save", data),
  submit: (id: string) => request.post<void>(`/outbound/${id}/submit`),
  approve: (id: string, pass: boolean) => request.post<void>(`/outbound/${id}/approve`, { pass }),
  confirm: (id: string) => request.post<void>(`/outbound/${id}/confirm`),
};
