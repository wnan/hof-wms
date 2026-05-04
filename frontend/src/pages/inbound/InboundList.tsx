import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { ExportButton } from "@/components/ExportButton";
import { Button } from "@/components/ui/button";
import { Plus, Upload } from "lucide-react";
import type { InboundOrder } from "@/types/inbound";
import { toast } from "sonner";
import { inboundApi } from "@/api/inbound";

const searchFields: SearchField[] = [
  { name: "code", label: "入库单号", type: "input" },
  { name: "supplier", label: "供应商", type: "input" },
  { name: "status", label: "状态", type: "select", options: [
    { label: "草稿", value: "draft" }, { label: "待审核", value: "pending" },
    { label: "已审核", value: "approved" }, { label: "已驳回", value: "rejected" },
    { label: "已入库", value: "stored" },
  ] },
  { name: "startDate", label: "开始日期", type: "date" },
  { name: "endDate", label: "结束日期", type: "date" },
];

const typeMap: Record<string, string> = { purchase: "采购入库", return: "退货入库", transfer: "调拨入库", other: "其他" };

export default function InboundList() {
  const nav = useNavigate();
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<string[]>([]);
  const [pageData, setPageData] = useState<{ records: InboundOrder[]; total: number; current: number; size: number }>({ records: [], total: 0, current: 1, size: 10 });

  useEffect(() => {
    inboundApi.list({ current: page, size: 10, ...query }).then(setPageData).catch(() => undefined);
  }, [page, query]);

  const columns: ColumnConfig<InboundOrder>[] = [
    { key: "code", title: "入库单号", render: r => <span className="font-medium text-primary">{r.code}</span> },
    { key: "supplier", title: "供应商" },
    { key: "type", title: "入库类型", render: r => typeMap[r.type] },
    { key: "totalAmount", title: "金额", align: "right", render: r => `¥${r.totalAmount.toLocaleString()}` },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status} /> },
    { key: "createdAt", title: "创建时间", render: r => <span className="text-muted-foreground text-sm">{r.createdAt}</span> },
  ];

  return (
    <div className="page-container">
      <PageHeader
        title="入库单列表"
        subtitle="管理所有入库订单，支持筛选、批量操作与导出"
        breadcrumbs={[{ label: "入库管理" }, { label: "入库单列表" }]}
        actions={
          <>
            <Button variant="outline" className="gap-1.5"><Upload className="h-4 w-4" />批量导入</Button>
            <ExportButton />
            <Button className="gap-1.5" onClick={() => nav("/inbound/detail/new")}><Plus className="h-4 w-4" />新建入库单</Button>
          </>
        }
      />
      <SearchBar fields={searchFields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro
        columns={columns}
        data={pageData.records}
        rowKey={r => r.id}
        selection
        selected={selected}
        onSelectionChange={setSelected}
        toolbar={
          <div className="flex items-center justify-between w-full">
            <span className="text-sm text-muted-foreground">已选 <span className="text-foreground font-medium">{selected.length}</span> 项</span>
            {selected.length > 0 && (
              <Button variant="outline" size="sm" onClick={() => { toast.success(`已批量删除 ${selected.length} 项`); setSelected([]); }}>批量删除</Button>
            )}
          </div>
        }
        pagination={{ current: pageData.current, size: pageData.size, total: pageData.total, onChange: setPage }}
        actions={[
          { label: "查看", onClick: r => nav(`/inbound/detail/${r.id}`) },
          { label: "编辑", show: r => r.status === "draft" || r.status === "rejected", onClick: r => nav(`/inbound/detail/${r.id}`) },
          { label: "删除", variant: "ghost", onClick: async r => { await inboundApi.remove(r.id); setPageData((prev) => ({ ...prev, records: prev.records.filter((item) => item.id !== r.id), total: Math.max(0, prev.total - 1) })); toast.success("已删除"); } },
        ]}
      />
    </div>
  );
}
