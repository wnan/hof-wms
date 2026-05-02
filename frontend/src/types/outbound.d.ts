export type OutboundStatus = "draft" | "pending" | "approved" | "shipped";
export type OutboundType = "sale" | "transfer" | "return" | "other";

export interface OutboundItem {
  id?: string;
  skuCode: string;
  skuName: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface OutboundOrder {
  id: string;
  code: string;
  customer: string;
  type: OutboundType;
  status: OutboundStatus;
  remark?: string;
  items: OutboundItem[];
  createdAt: string;
  totalAmount: number;
}
