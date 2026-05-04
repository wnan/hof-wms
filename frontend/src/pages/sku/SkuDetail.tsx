import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card } from "@/components/ui/card";
import { ArrowLeft, Save, Package, ImagePlus } from "lucide-react";
import type { SkuItem } from "@/types/sku";
import { toast } from "sonner";
import { skuApi } from "@/api/sku";

const empty: SkuItem = {
  id: "", skuCode: "", skuName: "", category: "3C数码", brand: "", unit: "件",
  spec: "", barcode: "", costPrice: 0, salePrice: 0, weight: 0, volume: 0,
  supplier: "", safetyStock: 50, status: "on", remark: "",
  createdAt: "", updatedAt: "",
};

export default function SkuDetail() {
  const { id } = useParams();
  const [search] = useSearchParams();
  const navigate = useNavigate();
  const isCreate = !id;
  const [editing, setEditing] = useState(isCreate || search.get("edit") === "1");
  const initial = useMemo(() => empty, []);
  const [form, setForm] = useState<SkuItem>(initial);

  useEffect(() => {
    if (!id) {
      setForm(empty);
      return;
    }
    skuApi.detail(id).then(setForm).catch(() => undefined);
  }, [id]);

  const set = <K extends keyof SkuItem>(k: K, v: SkuItem[K]) => setForm(p => ({ ...p, [k]: v }));

  const handleSave = async () => {
    if (!form.skuCode || !form.skuName) {
      toast.error("商品编码和名称为必填项");
      return;
    }
    if (isCreate) await skuApi.create(form);
    else await skuApi.update(form.id, form);
    toast.success(isCreate ? "商品创建成功" : "商品已更新");
    navigate("/sku/list");
  };

  return (
    <div className="page-container">
      <PageHeader
        title={isCreate ? "新增商品" : editing ? "编辑商品" : "商品详情"}
        breadcrumbs={[
          { label: "商品中心" },
          { label: "商品管理", to: "/sku/list" },
          { label: isCreate ? "新增" : form.skuName || "详情" },
        ]}
        actions={
          <>
            <Button variant="outline" className="gap-1.5" onClick={() => navigate("/sku/list")}>
              <ArrowLeft className="h-4 w-4" />返回
            </Button>
            {!editing && !isCreate && (
              <Button onClick={() => setEditing(true)}>编辑</Button>
            )}
            {editing && (
              <Button className="gap-1.5" onClick={handleSave}>
                <Save className="h-4 w-4" />保存
              </Button>
            )}
          </>
        }
      />

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <Card className="lg:col-span-1 p-5 space-y-4">
          <h3 className="text-sm font-semibold text-foreground">商品图片</h3>
          <div className="aspect-square bg-muted rounded-lg flex flex-col items-center justify-center text-muted-foreground border-2 border-dashed border-border">
            <Package className="h-16 w-16 opacity-30" />
            {editing && (
              <Button variant="ghost" size="sm" className="mt-3 gap-1.5">
                <ImagePlus className="h-4 w-4" />上传主图
              </Button>
            )}
          </div>
          <div className="text-xs text-muted-foreground space-y-1.5 pt-2 border-t border-border">
            <div className="flex justify-between"><span>创建时间</span><span className="text-foreground">{form.createdAt || "—"}</span></div>
            <div className="flex justify-between"><span>更新时间</span><span className="text-foreground">{form.updatedAt || "—"}</span></div>
          </div>
        </Card>

        <Card className="lg:col-span-2 p-5 space-y-5">
          <div>
            <h3 className="text-sm font-semibold text-foreground mb-4">基本信息</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <Field label="商品编码" required>
                <Input value={form.skuCode} disabled={!editing || !isCreate}
                  onChange={e => set("skuCode", e.target.value)} placeholder="如 SKU100001" />
              </Field>
              <Field label="商品名称" required>
                <Input value={form.skuName} disabled={!editing}
                  onChange={e => set("skuName", e.target.value)} />
              </Field>
              <Field label="分类">
                <Select value={form.category} disabled={!editing} onValueChange={v => set("category", v)}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {["3C数码", "家居日用", "服饰鞋包", "食品饮料", "美妆个护"].map(c => (
                      <SelectItem key={c} value={c}>{c}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </Field>
              <Field label="品牌">
                <Input value={form.brand} disabled={!editing} onChange={e => set("brand", e.target.value)} />
              </Field>
              <Field label="规格">
                <Input value={form.spec} disabled={!editing} onChange={e => set("spec", e.target.value)} />
              </Field>
              <Field label="单位">
                <Select value={form.unit} disabled={!editing} onValueChange={v => set("unit", v)}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {["件", "台", "盒", "瓶", "袋", "套"].map(c => (
                      <SelectItem key={c} value={c}>{c}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </Field>
              <Field label="条形码">
                <Input value={form.barcode} disabled={!editing} onChange={e => set("barcode", e.target.value)} />
              </Field>
              <Field label="供应商">
                <Input value={form.supplier} disabled={!editing} onChange={e => set("supplier", e.target.value)} />
              </Field>
            </div>
          </div>

          <div className="border-t border-border pt-5">
            <h3 className="text-sm font-semibold text-foreground mb-4">价格与规格</h3>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <Field label="成本价(¥)">
                <Input type="number" value={form.costPrice} disabled={!editing}
                  onChange={e => set("costPrice", Number(e.target.value))} />
              </Field>
              <Field label="售价(¥)">
                <Input type="number" value={form.salePrice} disabled={!editing}
                  onChange={e => set("salePrice", Number(e.target.value))} />
              </Field>
              <Field label="重量(kg)">
                <Input type="number" value={form.weight} disabled={!editing}
                  onChange={e => set("weight", Number(e.target.value))} />
              </Field>
              <Field label="体积(m³)">
                <Input type="number" value={form.volume} disabled={!editing}
                  onChange={e => set("volume", Number(e.target.value))} />
              </Field>
              <Field label="安全库存">
                <Input type="number" value={form.safetyStock} disabled={!editing}
                  onChange={e => set("safetyStock", Number(e.target.value))} />
              </Field>
              <Field label="状态">
                <Select value={form.status} disabled={!editing} onValueChange={v => set("status", v as "on" | "off")}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    <SelectItem value="on">上架</SelectItem>
                    <SelectItem value="off">下架</SelectItem>
                  </SelectContent>
                </Select>
              </Field>
            </div>
          </div>

          <div className="border-t border-border pt-5">
            <h3 className="text-sm font-semibold text-foreground mb-4">备注</h3>
            <Textarea rows={3} value={form.remark ?? ""} disabled={!editing}
              onChange={e => set("remark", e.target.value)} placeholder="选填" />
          </div>
        </Card>
      </div>
    </div>
  );
}

function Field({ label, required, children }: { label: string; required?: boolean; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs text-muted-foreground">
        {label}{required && <span className="text-destructive ml-0.5">*</span>}
      </Label>
      {children}
    </div>
  );
}
