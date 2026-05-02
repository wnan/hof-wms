import { useMemo, useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { ExportButton } from "@/components/ExportButton";
import { mockInventory, paginate } from "@/mock/data";
import type { InventoryItem } from "@/types/inventory";

const fields: SearchField[] = [
  { name: "skuCode", label: "商品编码", type: "input" },
  { name: "skuName", label: "商品名称", type: "input" },
  { name: "warehouse", label: "仓库", type: "select", options: [
    { label: "上海中心仓", value: "上海中心仓" }, { label: "广州前置仓", value: "广州前置仓" },
    { label: "成都分拣仓", value: "成都分拣仓" }, { label: "北京旗舰仓", value: "北京旗舰仓" },
  ] },
  { name: "category", label: "分类", type: "select", options: [
    { label: "3C数码", value: "3C数码" }, { label: "家居日用", value: "家居日用" },
    { label: "服饰鞋包", value: "服饰鞋包" }, { label: "食品饮料", value: "食品饮料" }, { label: "美妆个护", value: "美妆个护" },
  ] },
];

export default function InventoryList() {
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const filtered = useMemo(() => mockInventory.filter(o => {
    if (query.skuCode && !o.skuCode.includes(query.skuCode)) return false;
    if (query.skuName && !o.skuName.includes(query.skuName)) return false;
    if (query.warehouse && o.warehouse !== query.warehouse) return false;
    if (query.category && o.category !== query.category) return false;
    return true;
  }), [query]);
  const data = paginate(filtered, page, 12);

  const columns: ColumnConfig<InventoryItem>[] = [
    { key: "skuCode", title: "商品编码", render: r => <span className="font-medium text-primary">{r.skuCode}</span> },
    { key: "skuName", title: "商品名称" },
    { key: "category", title: "分类" },
    { key: "warehouse", title: "所在仓库" },
    { key: "stock", title: "当前库存", align: "right", render: r => <span className="font-medium">{r.stock}</span> },
    { key: "safetyStock", title: "安全库存", align: "right", render: r => <span className="text-muted-foreground">{r.safetyStock}</span> },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status} /> },
  ];
  return (
    <div className="page-container">
      <PageHeader title="库存查询" subtitle="实时查看各仓库 SKU 库存状态" breadcrumbs={[{ label: "库存管理" }, { label: "库存查询" }]} actions={<ExportButton />} />
      <SearchBar fields={fields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro columns={columns} data={data.records} rowKey={r => r.id}
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }} />
    </div>
  );
}
