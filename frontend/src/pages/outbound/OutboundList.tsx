import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { ExportButton } from "@/components/ExportButton";
import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";
import { mockOutbound, paginate } from "@/mock/data";
import type { OutboundOrder } from "@/types/outbound";
import { toast } from "sonner";

const searchFields: SearchField[] = [
  { name: "code", label: "出库单号", type: "input" },
  { name: "customer", label: "客户", type: "input" },
  { name: "status", label: "状态", type: "select", options: [
    { label: "草稿", value: "draft" }, { label: "待审核", value: "pending" },
    { label: "已审核", value: "approved" }, { label: "已出库", value: "shipped" },
  ] },
  { name: "startDate", label: "开始日期", type: "date" },
  { name: "endDate", label: "结束日期", type: "date" },
];
const typeMap: Record<string, string> = { sale: "销售出库", transfer: "调拨出库", return: "退货出库", other: "其他" };

export default function OutboundList() {
  const nav = useNavigate();
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);

  const filtered = useMemo(() => mockOutbound.filter(o => {
    if (query.code && !o.code.includes(query.code)) return false;
    if (query.customer && !o.customer.includes(query.customer)) return false;
    if (query.status && o.status !== query.status) return false;
    return true;
  }), [query]);
  const pageData = paginate(filtered, page, 10);

  const columns: ColumnConfig<OutboundOrder>[] = [
    { key: "code", title: "出库单号", render: r => <span className="font-medium text-primary">{r.code}</span> },
    { key: "customer", title: "客户" },
    { key: "type", title: "出库类型", render: r => typeMap[r.type] },
    { key: "totalAmount", title: "金额", align: "right", render: r => `¥${r.totalAmount.toLocaleString()}` },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status} /> },
    { key: "createdAt", title: "创建时间", render: r => <span className="text-muted-foreground text-sm">{r.createdAt}</span> },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="出库单列表"
        subtitle="管理所有出库与销售订单"
        breadcrumbs={[{ label: "出库销售" }, { label: "出库单列表" }]}
        actions={<><ExportButton /><Button className="gap-1.5" onClick={() => nav("/outbound/detail/new")}><Plus className="h-4 w-4" />新建出库单</Button></>}
      />
      <SearchBar fields={searchFields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro
        columns={columns} data={pageData.records} rowKey={r => r.id}
        pagination={{ current: pageData.current, size: pageData.size, total: pageData.total, onChange: setPage }}
        actions={[
          { label: "查看", onClick: r => nav(`/outbound/detail/${r.id}`) },
          { label: "编辑", show: r => r.status === "draft", onClick: r => nav(`/outbound/detail/${r.id}`) },
          { label: "删除", onClick: () => toast.success("已删除") },
        ]}
      />
    </div>
  );
}
