import { useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { mockAlerts, paginate } from "@/mock/data";
import type { StockAlert } from "@/types/inventory";
import { toast } from "sonner";
import { Settings2, AlertTriangle } from "lucide-react";

export default function InventoryAlert() {
  const [page, setPage] = useState(1);
  const [editing, setEditing] = useState<StockAlert | null>(null);
  const [threshold, setThreshold] = useState(0);
  const data = paginate(mockAlerts, page, 10);

  const columns: ColumnConfig<StockAlert>[] = [
    { key: "skuCode", title: "商品编码", render: r => <span className="font-medium text-primary">{r.skuCode}</span> },
    { key: "skuName", title: "商品名称" },
    { key: "stock", title: "当前库存", align: "right", render: r => <span className="font-medium text-destructive">{r.stock}</span> },
    { key: "safetyStock", title: "安全库存", align: "right" },
    { key: "alertType", title: "预警类型", render: r => <StatusBadge value={r.alertType} /> },
  ];

  return (
    <div className="page-container">
      <PageHeader title="库存预警" subtitle="实时监控库存不足与售罄商品" breadcrumbs={[{ label: "库存管理" }, { label: "库存预警" }]} />

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="stat-card flex items-center gap-4">
          <div className="h-11 w-11 rounded-md bg-warning-soft text-warning flex items-center justify-center"><AlertTriangle className="h-5 w-5" /></div>
          <div><p className="text-sm text-muted-foreground">库存不足</p><p className="text-xl font-semibold">{mockAlerts.filter(a => a.alertType === "low").length}</p></div>
        </div>
        <div className="stat-card flex items-center gap-4">
          <div className="h-11 w-11 rounded-md bg-destructive/10 text-destructive flex items-center justify-center"><AlertTriangle className="h-5 w-5" /></div>
          <div><p className="text-sm text-muted-foreground">已售罄</p><p className="text-xl font-semibold">{mockAlerts.filter(a => a.alertType === "out").length}</p></div>
        </div>
        <div className="stat-card flex items-center gap-4">
          <div className="h-11 w-11 rounded-md bg-info-soft text-info flex items-center justify-center"><Settings2 className="h-5 w-5" /></div>
          <div><p className="text-sm text-muted-foreground">总预警数</p><p className="text-xl font-semibold">{mockAlerts.length}</p></div>
        </div>
      </div>

      <TablePro columns={columns} data={data.records} rowKey={r => r.id}
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }}
        actions={[{ label: "设置阈值", onClick: r => { setEditing(r); setThreshold(r.safetyStock); } }]}
      />

      <Dialog open={!!editing} onOpenChange={v => !v && setEditing(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>设置安全库存阈值</DialogTitle></DialogHeader>
          {editing && (
            <div className="space-y-4">
              <div className="text-sm text-muted-foreground">商品：<span className="text-foreground font-medium">{editing.skuName}</span></div>
              <div className="space-y-1.5"><Label>安全库存</Label><Input type="number" value={threshold} onChange={e => setThreshold(Number(e.target.value))} /></div>
            </div>
          )}
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditing(null)}>取消</Button>
            <Button onClick={() => { toast.success("阈值已更新"); setEditing(null); }}>保存</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
