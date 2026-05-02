import { useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { ChevronRight, ChevronDown, Plus, Edit2, Trash2, FolderTree } from "lucide-react";
import { mockCategories } from "@/mock/sku";
import type { SkuCategory } from "@/types/sku";
import {
  Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";

interface FlatRow { node: SkuCategory; depth: number; }

function flatten(list: SkuCategory[], expanded: Set<string>, depth = 0): FlatRow[] {
  const rows: FlatRow[] = [];
  list.forEach(n => {
    rows.push({ node: n, depth });
    if (n.children?.length && expanded.has(n.id)) {
      rows.push(...flatten(n.children, expanded, depth + 1));
    }
  });
  return rows;
}

export default function SkuCategoryManage() {
  const [tree, setTree] = useState<SkuCategory[]>(mockCategories);
  const [expanded, setExpanded] = useState<Set<string>>(new Set(["C1", "C2", "C3"]));
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<SkuCategory | null>(null);
  const [parentId, setParentId] = useState<string | null>(null);
  const [form, setForm] = useState({ name: "", sort: 1 });

  const toggle = (id: string) => {
    const next = new Set(expanded);
    if (next.has(id)) next.delete(id); else next.add(id);
    setExpanded(next);
  };

  const openCreate = (pid: string | null) => {
    setEditing(null); setParentId(pid);
    setForm({ name: "", sort: 1 });
    setDialogOpen(true);
  };
  const openEdit = (n: SkuCategory) => {
    setEditing(n); setParentId(n.parentId);
    setForm({ name: n.name, sort: n.sort });
    setDialogOpen(true);
  };

  const save = () => {
    if (!form.name.trim()) { toast.error("请输入分类名称"); return; }
    if (editing) {
      const update = (list: SkuCategory[]): SkuCategory[] => list.map(n =>
        n.id === editing.id ? { ...n, name: form.name, sort: form.sort } :
        n.children ? { ...n, children: update(n.children) } : n
      );
      setTree(update(tree));
      toast.success("分类已更新");
    } else {
      const newNode: SkuCategory = {
        id: `C${Date.now()}`, name: form.name, parentId, sort: form.sort,
      };
      if (parentId === null) {
        setTree([...tree, newNode]);
      } else {
        const insert = (list: SkuCategory[]): SkuCategory[] => list.map(n =>
          n.id === parentId
            ? { ...n, children: [...(n.children ?? []), newNode] }
            : n.children ? { ...n, children: insert(n.children) } : n
        );
        setTree(insert(tree));
        setExpanded(new Set([...expanded, parentId]));
      }
      toast.success("分类已创建");
    }
    setDialogOpen(false);
  };

  const remove = (id: string) => {
    const del = (list: SkuCategory[]): SkuCategory[] =>
      list.filter(n => n.id !== id).map(n => n.children ? { ...n, children: del(n.children) } : n);
    setTree(del(tree));
    toast.success("分类已删除");
  };

  const rows = flatten(tree, expanded);

  return (
    <div className="page-container">
      <PageHeader
        title="商品分类"
        subtitle="多级分类树管理，支持新增子分类和拖动排序"
        breadcrumbs={[{ label: "商品中心" }, { label: "商品分类" }]}
        actions={
          <Button className="gap-1.5" onClick={() => openCreate(null)}>
            <Plus className="h-4 w-4" />新增一级分类
          </Button>
        }
      />
      <Card className="overflow-hidden">
        <div className="grid grid-cols-[1fr_120px_120px_240px] px-5 py-3 border-b border-border bg-muted/40 text-sm font-semibold text-foreground">
          <div>分类名称</div>
          <div className="text-center">排序</div>
          <div className="text-center">子分类数</div>
          <div className="text-right">操作</div>
        </div>
        {rows.length === 0 ? (
          <div className="py-16 flex flex-col items-center gap-2 text-muted-foreground">
            <FolderTree className="h-10 w-10 opacity-40" />
            <span className="text-sm">暂无分类</span>
          </div>
        ) : rows.map(({ node, depth }) => (
          <div key={node.id}
            className="grid grid-cols-[1fr_120px_120px_240px] px-5 py-3 border-b border-border last:border-b-0 hover:bg-muted/30 items-center text-sm">
            <div className="flex items-center gap-1" style={{ paddingLeft: depth * 24 }}>
              {node.children?.length ? (
                <Button variant="ghost" size="icon" className="h-6 w-6" onClick={() => toggle(node.id)}>
                  {expanded.has(node.id) ? <ChevronDown className="h-3.5 w-3.5" /> : <ChevronRight className="h-3.5 w-3.5" />}
                </Button>
              ) : <span className="w-6 inline-block" />}
              <FolderTree className="h-4 w-4 text-primary mr-1" />
              <span className="text-foreground font-medium">{node.name}</span>
            </div>
            <div className="text-center text-muted-foreground">{node.sort}</div>
            <div className="text-center text-muted-foreground">{node.children?.length ?? 0}</div>
            <div className="text-right flex justify-end gap-1">
              <Button variant="ghost" size="sm" className="gap-1" onClick={() => openCreate(node.id)}>
                <Plus className="h-3.5 w-3.5" />子分类
              </Button>
              <Button variant="ghost" size="sm" className="gap-1" onClick={() => openEdit(node)}>
                <Edit2 className="h-3.5 w-3.5" />编辑
              </Button>
              <Button variant="ghost" size="sm" className="gap-1 text-destructive hover:text-destructive" onClick={() => remove(node.id)}>
                <Trash2 className="h-3.5 w-3.5" />删除
              </Button>
            </div>
          </div>
        ))}
      </Card>

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{editing ? "编辑分类" : "新增分类"}</DialogTitle>
          </DialogHeader>
          <div className="space-y-4 py-2">
            <div className="space-y-1.5">
              <Label className="text-xs text-muted-foreground">分类名称</Label>
              <Input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="请输入分类名称" />
            </div>
            <div className="space-y-1.5">
              <Label className="text-xs text-muted-foreground">排序</Label>
              <Input type="number" value={form.sort} onChange={e => setForm({ ...form, sort: Number(e.target.value) })} />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setDialogOpen(false)}>取消</Button>
            <Button onClick={save}>保存</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
