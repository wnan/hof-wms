import { useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Trash2, Save, Send } from "lucide-react";
import type { StockCheckItem } from "@/types/inventory";
import { toast } from "sonner";
import { Badge } from "@/components/ui/badge";
import { inventoryApi } from "@/api/inventory";

export default function InventoryCheck() {
  const [code] = useState(`PD${Date.now()}`);
  const [warehouse, setWarehouse] = useState("上海中心仓");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [items, setItems] = useState<StockCheckItem[]>([
    { skuCode: "SKU010001", skuName: "智能手表 Pro v1", systemStock: 120, actualStock: 118, diff: -2 },
    { skuCode: "SKU010002", skuName: "无线降噪耳机 v2", systemStock: 65, actualStock: 65, diff: 0 },
  ]);
  const update = (i: number, patch: Partial<StockCheckItem>) => {
    const next = [...items];
    next[i] = { ...next[i], ...patch };
    next[i].diff = (next[i].actualStock || 0) - (next[i].systemStock || 0);
    setItems(next);
  };
  const add = () => setItems(p => [...p, { skuCode: "", skuName: "", systemStock: 0, actualStock: 0, diff: 0 }]);

  return (
    <div className="page-container">
      <PageHeader title="库存盘点" subtitle="录入实际库存并生成差异报告" breadcrumbs={[{ label: "库存管理" }, { label: "库存盘点" }]} />
      <div className="panel">
        <h3 className="font-semibold mb-4">盘点信息</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5"><Label>盘点单号</Label><Input value={code} disabled /></div>
          <div className="space-y-1.5">
            <Label>仓库</Label>
            <Select value={warehouse} onValueChange={setWarehouse}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="上海中心仓">上海中心仓</SelectItem><SelectItem value="广州前置仓">广州前置仓</SelectItem>
                <SelectItem value="成都分拣仓">成都分拣仓</SelectItem><SelectItem value="北京旗舰仓">北京旗舰仓</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5"><Label>盘点日期</Label><Input type="date" value={date} onChange={e => setDate(e.target.value)} /></div>
        </div>
      </div>
      <div className="panel">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold">盘点明细</h3>
          <Button size="sm" variant="outline" onClick={add} className="gap-1.5"><Plus className="h-4 w-4" />添加商品</Button>
        </div>
        <Table>
          <TableHeader><TableRow className="bg-muted/40 hover:bg-muted/40">
            <TableHead className="w-12">#</TableHead><TableHead>商品编码</TableHead><TableHead>商品名称</TableHead>
            <TableHead className="w-32 text-right">系统库存</TableHead><TableHead className="w-32 text-right">实际库存</TableHead>
            <TableHead className="w-28 text-right">差异</TableHead><TableHead className="w-16 text-right">操作</TableHead>
          </TableRow></TableHeader>
          <TableBody>
            {items.map((it, i) => (
              <TableRow key={i}>
                <TableCell className="text-muted-foreground">{i + 1}</TableCell>
                <TableCell><Input value={it.skuCode} onChange={e => update(i, { skuCode: e.target.value })} /></TableCell>
                <TableCell><Input value={it.skuName} onChange={e => update(i, { skuName: e.target.value })} /></TableCell>
                <TableCell><Input type="number" className="text-right" value={it.systemStock} onChange={e => update(i, { systemStock: Number(e.target.value) })} /></TableCell>
                <TableCell><Input type="number" className="text-right" value={it.actualStock} onChange={e => update(i, { actualStock: Number(e.target.value) })} /></TableCell>
                <TableCell className="text-right">
                  {it.diff === 0 ? <Badge variant="outline" className="bg-success-soft text-success border-transparent">一致</Badge>
                    : it.diff > 0 ? <Badge variant="outline" className="bg-info-soft text-info border-transparent">+{it.diff}</Badge>
                    : <Badge variant="outline" className="bg-destructive/10 text-destructive border-transparent">{it.diff}</Badge>}
                </TableCell>
                <TableCell className="text-right"><Button size="icon" variant="ghost" onClick={() => setItems(p => p.filter((_, idx) => idx !== i))}><Trash2 className="h-4 w-4 text-destructive" /></Button></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
      <div className="panel flex items-center justify-end gap-2">
        <Button variant="outline" onClick={async () => { await inventoryApi.checkSave({ code, warehouse, checkDate: date, items, status: "draft" }); toast.success("已保存"); }} className="gap-1.5"><Save className="h-4 w-4" />保存</Button>
        <Button onClick={async () => { const saved = await inventoryApi.checkSave({ code, warehouse, checkDate: date, items, status: "draft" }); await inventoryApi.checkSubmit(saved.id); toast.success("盘点已提交"); }} className="gap-1.5"><Send className="h-4 w-4" />提交盘点</Button>
      </div>
    </div>
  );
}
