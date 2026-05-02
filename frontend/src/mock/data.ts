// 离线 mock 数据，待真实后端就绪后通过 VITE_API_BASE_URL 切换
import type { InboundOrder } from "@/types/inbound";
import type { OutboundOrder } from "@/types/outbound";
import type { InventoryItem, StockAlert } from "@/types/inventory";
import type { SyncTask, SyncLog } from "@/types/data-sync";

const suppliers = ["华东供应链", "深圳精工电子", "杭州源头工厂", "上海国际贸易", "广州优品商贸"];
const customers = ["京东物流", "天猫旗舰", "线下经销商A", "海外代理商", "企业团购"];
const warehouses = ["上海中心仓", "广州前置仓", "成都分拣仓", "北京旗舰仓"];
const categories = ["3C数码", "家居日用", "服饰鞋包", "食品饮料", "美妆个护"];

const skus = Array.from({ length: 30 }, (_, i) => ({
  code: `SKU${String(10001 + i).padStart(6, "0")}`,
  name: ["智能手表 Pro", "无线降噪耳机", "便携充电宝", "极简办公椅", "保温杯 500ml", "亚麻床品四件套"][i % 6] + ` v${(i % 3) + 1}`,
  category: categories[i % categories.length],
}));

function pick<T>(arr: T[], i: number): T { return arr[i % arr.length]; }
function dt(daysAgo: number) { return new Date(Date.now() - daysAgo * 86400000).toISOString().slice(0, 19).replace("T", " "); }

export const mockInbound: InboundOrder[] = Array.from({ length: 28 }, (_, i) => {
  const items = Array.from({ length: (i % 3) + 2 }, (_, j) => {
    const sku = skus[(i + j) % skus.length];
    const qty = (j + 1) * 10 + i;
    const price = 80 + ((i * j) % 200);
    return { id: `${i}-${j}`, skuCode: sku.code, skuName: sku.name, quantity: qty, price, subtotal: qty * price };
  });
  const total = items.reduce((s, x) => s + x.subtotal, 0);
  const statuses = ["draft", "pending", "approved", "stored", "rejected"] as const;
  return {
    id: `IN${1000 + i}`,
    code: `RK${dt(i).slice(0, 10).replace(/-/g, "")}${String(i).padStart(3, "0")}`,
    supplier: pick(suppliers, i),
    type: (["purchase", "return", "transfer", "other"] as const)[i % 4],
    status: statuses[i % statuses.length],
    remark: i % 3 === 0 ? "加急处理" : "",
    items, totalAmount: total,
    createdAt: dt(i),
  };
});

export const mockOutbound: OutboundOrder[] = Array.from({ length: 26 }, (_, i) => {
  const items = Array.from({ length: (i % 3) + 1 }, (_, j) => {
    const sku = skus[(i * 2 + j) % skus.length];
    const qty = (j + 1) * 5 + i;
    const price = 120 + ((i * j) % 180);
    return { id: `${i}-${j}`, skuCode: sku.code, skuName: sku.name, quantity: qty, price, subtotal: qty * price };
  });
  const total = items.reduce((s, x) => s + x.subtotal, 0);
  const statuses = ["draft", "pending", "approved", "shipped"] as const;
  return {
    id: `OUT${2000 + i}`,
    code: `CK${dt(i).slice(0, 10).replace(/-/g, "")}${String(i).padStart(3, "0")}`,
    customer: pick(customers, i),
    type: (["sale", "transfer", "return", "other"] as const)[i % 4],
    status: statuses[i % statuses.length],
    items, totalAmount: total,
    createdAt: dt(i),
  };
});

export const mockInventory: InventoryItem[] = skus.map((s, i) => {
  const stock = (i * 37) % 500;
  const safety = 50;
  return {
    id: `INV${i}`,
    skuCode: s.code, skuName: s.name, category: s.category,
    warehouse: pick(warehouses, i),
    stock, safetyStock: safety,
    status: stock === 0 ? "out" : stock < safety ? "low" : "normal",
  };
});

export const mockAlerts: StockAlert[] = mockInventory
  .filter(x => x.status !== "normal")
  .map(x => ({ id: x.id, skuCode: x.skuCode, skuName: x.skuName, stock: x.stock, safetyStock: x.safetyStock, alertType: x.status === "out" ? "out" : "low" }));

export const mockSyncTasks: SyncTask[] = [
  { id: "T1", name: "ERP 商品主数据同步", externalSystem: "金蝶 ERP", endpoint: "https://erp.example.com/api/sku", authType: "bearer", syncType: "incremental", triggerType: "schedule", cron: "0 */30 * * * ?", mappings: [{ source: "sku_id", target: "skuCode" }, { source: "sku_name", target: "skuName" }], status: "success", lastRunAt: dt(0) },
  { id: "T2", name: "电商订单回写", externalSystem: "Shopify", endpoint: "https://api.shopify.com/orders", authType: "apikey", syncType: "incremental", triggerType: "schedule", cron: "0 0 * * * ?", mappings: [], status: "running", lastRunAt: dt(0) },
  { id: "T3", name: "WMS 库存上报", externalSystem: "集团数据中台", endpoint: "https://dc.example.com/inventory", authType: "bearer", syncType: "full", triggerType: "manual", mappings: [], status: "failed", lastRunAt: dt(1) },
  { id: "T4", name: "供应商目录同步", externalSystem: "SRM 系统", endpoint: "https://srm.example.com/suppliers", authType: "basic", syncType: "full", triggerType: "schedule", cron: "0 0 2 * * ?", mappings: [], status: "idle" },
];

export const mockSyncLogs: SyncLog[] = Array.from({ length: 18 }, (_, i) => ({
  id: `L${i}`,
  taskName: pick(mockSyncTasks, i).name,
  startAt: dt(i),
  endAt: dt(i),
  count: ((i + 1) * 137) % 5000,
  status: (["success", "failed", "running"] as const)[i % 3],
  error: i % 3 === 1 ? "连接超时：30s 内未收到响应" : undefined,
}));

export function paginate<T>(rows: T[], current = 1, size = 10) {
  const start = (current - 1) * size;
  return { records: rows.slice(start, start + size), total: rows.length, current, size };
}
