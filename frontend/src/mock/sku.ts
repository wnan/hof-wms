import type { SkuItem, SkuCategory } from "@/types/sku";

const brands = ["小米", "华为", "Apple", "网易严选", "无印良品", "雀巢", "雅诗兰黛"];
const units = ["件", "台", "盒", "瓶", "袋"];
const suppliers = ["华东供应链", "深圳精工电子", "杭州源头工厂", "上海国际贸易", "广州优品商贸"];
const categories = ["3C数码", "家居日用", "服饰鞋包", "食品饮料", "美妆个护"];
const names = [
  "智能手表 Pro", "无线降噪耳机", "便携充电宝", "极简办公椅", "保温杯 500ml",
  "亚麻床品四件套", "记忆棉枕头", "蓝牙音箱", "无线键盘", "护手霜套装",
];

function dt(daysAgo: number) {
  return new Date(Date.now() - daysAgo * 86400000).toISOString().slice(0, 19).replace("T", " ");
}

export const mockSkus: SkuItem[] = Array.from({ length: 36 }, (_, i) => {
  const cost = 50 + ((i * 17) % 400);
  const sale = Math.round(cost * (1.3 + (i % 5) * 0.1));
  return {
    id: `SK${1000 + i}`,
    skuCode: `SKU${String(10001 + i).padStart(6, "0")}`,
    skuName: `${names[i % names.length]} v${(i % 3) + 1}`,
    category: categories[i % categories.length],
    brand: brands[i % brands.length],
    unit: units[i % units.length],
    spec: `规格-${["S", "M", "L", "XL"][i % 4]} / ${["黑", "白", "蓝", "灰"][i % 4]}`,
    barcode: `69${String(1000000000 + i * 137).slice(0, 11)}`,
    costPrice: cost,
    salePrice: sale,
    weight: Number(((i % 10) + 0.5).toFixed(2)),
    volume: Number((((i % 8) + 1) * 0.01).toFixed(3)),
    supplier: suppliers[i % suppliers.length],
    safetyStock: 50 + (i % 5) * 20,
    status: i % 7 === 0 ? "off" : "on",
    remark: i % 4 === 0 ? "热销商品" : "",
    createdAt: dt(i + 5),
    updatedAt: dt(i),
  };
});

export const mockCategories: SkuCategory[] = [
  { id: "C1", name: "3C数码", parentId: null, sort: 1, children: [
    { id: "C1-1", name: "手机配件", parentId: "C1", sort: 1 },
    { id: "C1-2", name: "智能穿戴", parentId: "C1", sort: 2 },
    { id: "C1-3", name: "影音娱乐", parentId: "C1", sort: 3 },
  ]},
  { id: "C2", name: "家居日用", parentId: null, sort: 2, children: [
    { id: "C2-1", name: "家具家装", parentId: "C2", sort: 1 },
    { id: "C2-2", name: "厨房用品", parentId: "C2", sort: 2 },
    { id: "C2-3", name: "床上用品", parentId: "C2", sort: 3 },
  ]},
  { id: "C3", name: "服饰鞋包", parentId: null, sort: 3, children: [
    { id: "C3-1", name: "男装", parentId: "C3", sort: 1 },
    { id: "C3-2", name: "女装", parentId: "C3", sort: 2 },
  ]},
  { id: "C4", name: "食品饮料", parentId: null, sort: 4 },
  { id: "C5", name: "美妆个护", parentId: null, sort: 5 },
];
