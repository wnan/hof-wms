import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Plus, Trash2, ArrowLeft, Save, Send, Check, X } from "lucide-react";
import { StatusBadge } from "@/components/StatusBadge";
import type { InboundItem, InboundOrder } from "@/types/inbound";
import { toast } from "sonner";
import { inboundApi } from "@/api/inbound";

export default function InboundDetail() {
  const { id } = useParams();
  const nav = useNavigate();
  const isNew = !id || id === "new";
  const initial = useMemo<InboundOrder>(() => {
    if (isNew) {
      return { id: "", code: `RK${Date.now()}`, supplier: "", type: "purchase", status: "draft", remark: "", items: [], createdAt: new Date().toISOString().slice(0, 10), totalAmount: 0 };
    }
    return { id: "", code: "", supplier: "", type: "purchase", status: "draft", remark: "", items: [], createdAt: "", totalAmount: 0 };
  }, [id, isNew]);

  const [form, setForm] = useState<InboundOrder>(initial);
  useEffect(() => {
    if (!isNew && id) inboundApi.detail(id).then(setForm).catch(() => undefined);
  }, [id, isNew]);
  const total = form.items.reduce((s, x) => s + x.subtotal, 0);

  const update = <K extends keyof InboundOrder>(k: K, v: InboundOrder[K]) => setForm(p => ({ ...p, [k]: v }));
  const updateItem = (i: number, patch: Partial<InboundItem>) => {
    const items = [...form.items];
    items[i] = { ...items[i], ...patch };
    items[i].subtotal = (items[i].quantity || 0) * (items[i].price || 0);
    setForm(p => ({ ...p, items }));
  };
  const addItem = () => setForm(p => ({ ...p, items: [...p.items, { skuCode: "", skuName: "", quantity: 1, price: 0, subtotal: 0 }] }));
  const removeItem = (i: number) => setForm(p => ({ ...p, items: p.items.filter((_, idx) => idx !== i) }));

  return (
    <div className="page-container">
      <PageHeader
        title={isNew ? "新建入库单" : `入库单 ${form.code}`}
        breadcrumbs={[{ label: "入库管理", to: "/inbound/list" }, { label: "入库单列表", to: "/inbound/list" }, { label: isNew ? "新建" : form.code }]}
        actions={
          <>
            <Button variant="outline" onClick={() => nav("/inbound/list")} className="gap-1.5"><ArrowLeft className="h-4 w-4" />返回</Button>
            {!isNew && <StatusBadge value={form.status} />}
          </>
        }
      />

      <div className="panel">
        <h3 className="font-semibold mb-4">基本信息</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5"><Label>入库单号</Label><Input value={form.code} disabled /></div>
          <div className="space-y-1.5">
            <Label>供应商 <span className="text-destructive">*</span></Label>
            <Input value={form.supplier} onChange={e => update("supplier", e.target.value)} placeholder="请输入供应商名称" />
          </div>
          <div className="space-y-1.5">
            <Label>入库类型</Label>
            <Select value={form.type} onValueChange={v => update("type", v as InboundOrder["type"])}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="purchase">采购入库</SelectItem>
                <SelectItem value="return">退货入库</SelectItem>
                <SelectItem value="transfer">调拨入库</SelectItem>
                <SelectItem value="other">其他</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <div className="md:col-span-3 space-y-1.5">
            <Label>备注</Label>
            <Textarea value={form.remark ?? ""} onChange={e => update("remark", e.target.value)} placeholder="备注信息..." rows={2} />
          </div>
        </div>
      </div>

      <div className="panel">
        <div className="flex items-center justify-between mb-4">
          <h3 className="font-semibold">商品明细</h3>
          <Button size="sm" variant="outline" onClick={addItem} className="gap-1.5"><Plus className="h-4 w-4" />添加商品</Button>
        </div>
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              <TableHead className="w-12">#</TableHead>
              <TableHead>商品编码</TableHead>
              <TableHead>商品名称</TableHead>
              <TableHead className="w-28 text-right">数量</TableHead>
              <TableHead className="w-32 text-right">单价</TableHead>
              <TableHead className="w-32 text-right">小计</TableHead>
              <TableHead className="w-16 text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {form.items.length === 0 && (
              <TableRow><TableCell colSpan={7} className="text-center py-8 text-muted-foreground text-sm">暂无商品，点击右上角"添加商品"</TableCell></TableRow>
            )}
            {form.items.map((it, i) => (
              <TableRow key={i}>
                <TableCell className="text-muted-foreground">{i + 1}</TableCell>
                <TableCell><Input value={it.skuCode} onChange={e => updateItem(i, { skuCode: e.target.value })} placeholder="SKU 编码" /></TableCell>
                <TableCell><Input value={it.skuName} onChange={e => updateItem(i, { skuName: e.target.value })} placeholder="商品名称" /></TableCell>
                <TableCell><Input type="number" min={0} className="text-right" value={it.quantity} onChange={e => updateItem(i, { quantity: Number(e.target.value) })} /></TableCell>
                <TableCell><Input type="number" min={0} className="text-right" value={it.price} onChange={e => updateItem(i, { price: Number(e.target.value) })} /></TableCell>
                <TableCell className="text-right font-medium">¥{it.subtotal.toLocaleString()}</TableCell>
                <TableCell className="text-right"><Button size="icon" variant="ghost" onClick={() => removeItem(i)}><Trash2 className="h-4 w-4 text-destructive" /></Button></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
        <div className="flex justify-end mt-4 pt-4 border-t border-border">
          <div className="text-right">
            <p className="text-xs text-muted-foreground">合计金额</p>
            <p className="text-2xl font-semibold text-primary">¥{total.toLocaleString()}</p>
          </div>
        </div>
      </div>

      <div className="panel flex items-center justify-end gap-2">
        <Button variant="outline" onClick={async () => { const saved = await inboundApi.save({ ...form, totalAmount: total }); setForm(saved); toast.success("草稿已保存"); }} className="gap-1.5"><Save className="h-4 w-4" />保存草稿</Button>
        <Button variant="outline" onClick={async () => { const saved = await inboundApi.save({ ...form, totalAmount: total }); await inboundApi.submit(saved.id); setForm({ ...saved, status: "pending" }); toast.success("已提交审核"); }} className="gap-1.5"><Send className="h-4 w-4" />提交审核</Button>
        {form.status === "pending" && (
          <>
            <Button variant="outline" onClick={async () => { await inboundApi.approve(form.id, false); setForm({ ...form, status: "rejected" }); toast.error("已驳回"); }} className="gap-1.5"><X className="h-4 w-4" />审核驳回</Button>
            <Button onClick={async () => { await inboundApi.approve(form.id, true); setForm({ ...form, status: "approved" }); toast.success("审核通过"); }} className="gap-1.5"><Check className="h-4 w-4" />审核通过</Button>
          </>
        )}
        {form.status === "approved" && (
          <Button onClick={async () => { await inboundApi.confirm(form.id); setForm({ ...form, status: "stored" }); toast.success("已确认入库"); }} className="gap-1.5"><Check className="h-4 w-4" />确认入库</Button>
        )}
      </div>
    </div>
  );
}
