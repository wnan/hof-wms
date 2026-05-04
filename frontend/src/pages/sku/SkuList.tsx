import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig, type ActionConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { ExportButton } from "@/components/ExportButton";
import { Button } from "@/components/ui/button";
import { Plus, Upload, Trash2, Package } from "lucide-react";
import type { SkuItem } from "@/types/sku";
import { toast } from "sonner";
import { skuApi } from "@/api/sku";
import {
  AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent,
  AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle,
} from "@/components/ui/alert-dialog";

const fields: SearchField[] = [
  { name: "skuCode", label: "商品编码", type: "input" },
  { name: "skuName", label: "商品名称", type: "input" },
  { name: "category", label: "分类", type: "select", options: [
    { label: "3C数码", value: "3C数码" }, { label: "家居日用", value: "家居日用" },
    { label: "服饰鞋包", value: "服饰鞋包" }, { label: "食品饮料", value: "食品饮料" }, { label: "美妆个护", value: "美妆个护" },
  ]},
  { name: "brand", label: "品牌", type: "input" },
  { name: "status", label: "状态", type: "select", options: [
    { label: "上架", value: "on" }, { label: "下架", value: "off" },
  ]},
];

export default function SkuList() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const [selected, setSelected] = useState<string[]>([]);
  const [delTarget, setDelTarget] = useState<SkuItem | null>(null);
  const [batchDelOpen, setBatchDelOpen] = useState(false);
  const [data, setData] = useState<{ records: SkuItem[]; total: number; current: number; size: number }>({ records: [], total: 0, current: 1, size: 12 });

  useEffect(() => {
    skuApi.list({ current: page, size: 12, ...query }).then(setData).catch(() => undefined);
  }, [page, query]);

  const columns: ColumnConfig<SkuItem>[] = [
    { key: "skuCode", title: "商品编码", width: 140, render: r => <span className="font-medium text-primary">{r.skuCode}</span> },
    { key: "skuName", title: "商品名称", render: r => (
      <div className="flex items-center gap-2">
        <div className="h-9 w-9 rounded bg-muted flex items-center justify-center shrink-0">
          <Package className="h-4 w-4 text-muted-foreground" />
        </div>
        <div className="min-w-0">
          <div className="text-sm text-foreground truncate">{r.skuName}</div>
          <div className="text-xs text-muted-foreground truncate">{r.spec}</div>
        </div>
      </div>
    )},
    { key: "category", title: "分类", width: 110 },
    { key: "brand", title: "品牌", width: 100 },
    { key: "unit", title: "单位", width: 70, align: "center" },
    { key: "costPrice", title: "成本价", width: 100, align: "right", render: r => <span className="text-muted-foreground">¥{r.costPrice.toFixed(2)}</span> },
    { key: "salePrice", title: "售价", width: 100, align: "right", render: r => <span className="font-medium text-foreground">¥{r.salePrice.toFixed(2)}</span> },
    { key: "safetyStock", title: "安全库存", width: 90, align: "right" },
    { key: "status", title: "状态", width: 90, render: r => (
      <StatusBadge value={r.status === "on" ? "approved" : "muted"} />
    )},
    { key: "updatedAt", title: "更新时间", width: 160, render: r => <span className="text-muted-foreground text-xs">{r.updatedAt}</span> },
  ];

  const realActions: ActionConfig<SkuItem>[] = [
    { label: "查看", onClick: r => navigate(`/sku/detail/${r.id}`) },
    { label: "编辑", onClick: r => navigate(`/sku/detail/${r.id}?edit=1`) },
    {
      label: "上/下架",
      onClick: async r => {
        await skuApi.toggleStatus(r.id, r.status === "on" ? "off" : "on");
        setData((prev) => ({
          ...prev,
          records: prev.records.map((item) => item.id === r.id ? { ...item, status: item.status === "on" ? "off" : "on" } : item),
        }));
        toast.success(`已${r.status === "on" ? "下架" : "上架"}：${r.skuName}`);
      },
    },
    { label: "删除", variant: "ghost", onClick: r => setDelTarget(r) },
  ];

  const confirmDelete = () => {
    if (!delTarget) return;
    skuApi.remove(delTarget.id).then(() => {
      setData((prev) => ({ ...prev, records: prev.records.filter((x) => x.id !== delTarget.id), total: Math.max(0, prev.total - 1) }));
      toast.success(`已删除：${delTarget.skuName}`);
      setDelTarget(null);
    });
  };

  const confirmBatchDelete = () => {
    skuApi.batchRemove(selected).then(() => {
      setData((prev) => ({ ...prev, records: prev.records.filter((x) => !selected.includes(x.id)), total: Math.max(0, prev.total - selected.length) }));
      toast.success(`已删除 ${selected.length} 条商品`);
      setSelected([]);
      setBatchDelOpen(false);
    });
  };

  return (
    <div className="page-container">
      <PageHeader
        title="商品管理"
        subtitle="维护 SKU 主数据，支持上下架、批量导入与导出"
        breadcrumbs={[{ label: "商品中心" }, { label: "商品管理" }]}
        actions={
          <>
            <Button variant="outline" className="gap-1.5" onClick={() => toast.info("请选择 Excel 文件导入")}>
              <Upload className="h-4 w-4" />批量导入
            </Button>
            <ExportButton />
            <Button className="gap-1.5" onClick={() => navigate("/sku/detail")}>
              <Plus className="h-4 w-4" />新增商品
            </Button>
          </>
        }
      />
      <SearchBar fields={fields} values={filters} onChange={setFilters}
        onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro
        columns={columns}
        data={data.records}
        rowKey={r => r.id}
        selection
        selected={selected}
        onSelectionChange={setSelected}
        actions={realActions}
        toolbar={
          selected.length > 0 ? (
            <div className="flex items-center gap-3">
              <span className="text-sm text-muted-foreground">已选 <span className="text-primary font-medium">{selected.length}</span> 项</span>
              <Button variant="outline" size="sm" className="gap-1.5" onClick={() => setBatchDelOpen(true)}>
                <Trash2 className="h-4 w-4" />批量删除
              </Button>
            </div>
          ) : (
            <span className="text-sm text-muted-foreground">共 {data.total} 条 SKU</span>
          )
        }
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }}
      />

      <AlertDialog open={!!delTarget} onOpenChange={v => !v && setDelTarget(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>删除商品</AlertDialogTitle>
            <AlertDialogDescription>
              确认删除「{delTarget?.skuName}」？删除后无法恢复，且会影响关联的库存与单据。
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>取消</AlertDialogCancel>
            <AlertDialogAction onClick={confirmDelete}>确认删除</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      <AlertDialog open={batchDelOpen} onOpenChange={setBatchDelOpen}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>批量删除</AlertDialogTitle>
            <AlertDialogDescription>
              将删除已选中的 {selected.length} 条商品，是否继续？
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>取消</AlertDialogCancel>
            <AlertDialogAction onClick={confirmBatchDelete}>确认删除</AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
