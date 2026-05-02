export interface InventoryItem {
  id: string;
  skuCode: string;
  skuName: string;
  category: string;
  warehouse: string;
  stock: number;
  safetyStock: number;
  status: "normal" | "low" | "out";
}

export interface StockCheckItem {
  skuCode: string;
  skuName: string;
  systemStock: number;
  actualStock: number;
  diff: number;
}

export interface StockCheck {
  id: string;
  code: string;
  warehouse: string;
  checkDate: string;
  items: StockCheckItem[];
  status: "draft" | "submitted";
}

export interface StockAlert {
  id: string;
  skuCode: string;
  skuName: string;
  stock: number;
  safetyStock: number;
  alertType: "low" | "out";
}
