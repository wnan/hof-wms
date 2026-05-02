export type InboundStatus = "draft" | "pending" | "approved" | "rejected" | "stored";
export type InboundType = "purchase" | "return" | "transfer" | "other";

export interface InboundItem {
  id?: string;
  skuCode: string;
  skuName: string;
  quantity: number;
  price: number;
  subtotal: number;
}

export interface InboundOrder {
  id: string;
  code: string;
  supplier: string;
  type: InboundType;
  status: InboundStatus;
  remark?: string;
  items: InboundItem[];
  createdAt: string;
  totalAmount: number;
}
