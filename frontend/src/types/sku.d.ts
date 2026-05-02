export interface SkuItem {
  id: string;
  skuCode: string;
  skuName: string;
  category: string;
  brand: string;
  unit: string;
  spec: string;
  barcode: string;
  costPrice: number;
  salePrice: number;
  weight: number;
  volume: number;
  imageUrl?: string;
  supplier: string;
  safetyStock: number;
  status: "on" | "off";
  remark?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SkuCategory {
  id: string;
  name: string;
  parentId: string | null;
  sort: number;
  children?: SkuCategory[];
}
