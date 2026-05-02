import { useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Plus, Trash2, ChevronRight, ChevronDown, Menu as MenuIcon, MousePointerClick, Cable } from "lucide-react";
import { mockPermissions } from "@/mock/rbac";
import type { SysPermission, PermissionType } from "@/types/rbac";
import { toast } from "sonner";

const typeMeta: Record<PermissionType, { label: string; icon: React.ComponentType<{ className?: string }>; tone: string }> = {
  menu: { label: "菜单", icon: MenuIcon, tone: "bg-primary-soft text-primary" },
  button: { label: "按钮", icon: MousePointerClick, tone: "bg-info-soft text-info" },
  api: { label: "接口", icon: Cable, tone: "bg-warning-soft text-warning" },
};

function PermissionRow({ node, level, expanded, onToggle, onAdd, onEdit, onDelete }: {
  node: SysPermission; level: number;
  expanded: Set<string>;
  onToggle: (id: string) => void;
  onAdd: (parentId: string) => void;
  onEdit: (n: SysPermission) => void;
  onDelete: (n: SysPermission) => void;
}) {
  const has = !!node.children?.length;
  const open = expanded.has(node.id);
  const meta = typeMeta[node.type];
  const Icon = meta.icon;
  return (
    <>
      <TableRow>
        <TableCell>
          <div className="flex items-center" style={{ paddingLeft: level * 20 }}>
            {has ? (
              <button onClick={() => onToggle(node.id)} className="p-0.5 hover:bg-muted rounded">
                {open ? <ChevronDown className="h-3.5 w-3.5" /> : <ChevronRight className="h-3.5 w-3.5" />}
              </button>
            ) : <span className="w-5" />}
            <div className={`h-6 w-6 rounded flex items-center justify-center ml-1 mr-2 ${meta.tone}`}><Icon className="h-3.5 w-3.5" /></div>
            <span className="text-sm font-medium">{node.name}</span>
          </div>
        </TableCell>
        <TableCell><span className="text-xs font-mono text-muted-foreground">{node.code}</span></TableCell>
        <TableCell><span className={`text-[11px] px-2 py-0.5 rounded ${meta.tone}`}>{meta.label}</span></TableCell>
        <TableCell><span className="text-xs text-muted-foreground">{node.path ?? "—"}</span></TableCell>
        <TableCell className="text-right">{node.sort}</TableCell>
        <TableCell className="text-right">
          <div className="flex justify-end gap-1">
            <Button size="sm" variant="ghost" onClick={() => onAdd(node.id)}><Plus className="h-3.5 w-3.5" /></Button>
            <Button size="sm" variant="ghost" onClick={() => onEdit(node)}>编辑</Button>
            <Button size="sm" variant="ghost" onClick={() => onDelete(node)}><Trash2 className="h-3.5 w-3.5 text-destructive" /></Button>
          </div>
        </TableCell>
      </TableRow>
      {has && open && node.children!.map(c => (
        <PermissionRow key={c.id} node={c} level={level + 1} expanded={expanded}
          onToggle={onToggle} onAdd={onAdd} onEdit={onEdit} onDelete={onDelete} />
      ))}
    </>
  );
}

// 简单 deep clone & 树操作
function cloneTree(nodes: SysPermission[]): SysPermission[] {
  return nodes.map(n => ({ ...n, children: n.children ? cloneTree(n.children) : undefined }));
}
function findNode(nodes: SysPermission[], id: string): SysPermission | null {
  for (const n of nodes) {
    if (n.id === id) return n;
    if (n.children) { const r = findNode(n.children, id); if (r) return r; }
  }
  return null;
}
function removeNode(nodes: SysPermission[], id: string): SysPermission[] {
  return nodes.filter(n => n.id !== id).map(n => ({ ...n, children: n.children ? removeNode(n.children, id) : undefined }));
}

const emptyForm: Partial<SysPermission> = { name: "", code: "", type: "menu", path: "", sort: 1, parentId: null };

export default function PermissionManage() {
  const [tree, setTree] = useState<SysPermission[]>(cloneTree(mockPermissions));
  const [expanded, setExpanded] = useState<Set<string>>(new Set(mockPermissions.map(n => n.id)));
  const [editing, setEditing] = useState<Partial<SysPermission> | null>(null);
  const [isNew, setIsNew] = useState(false);

  const toggle = (id: string) => {
    const next = new Set(expanded);
    next.has(id) ? next.delete(id) : next.add(id);
    setExpanded(next);
  };
  const expandAll = () => {
    const all = new Set<string>();
    const walk = (ns: SysPermission[]) => ns.forEach(n => { all.add(n.id); n.children && walk(n.children); });
    walk(tree); setExpanded(all);
  };

  const onAdd = (parentId: string | null) => {
    setIsNew(true);
    setEditing({ ...emptyForm, parentId });
  };
  const onEdit = (n: SysPermission) => { setIsNew(false); setEditing({ ...n }); };
  const onDelete = (n: SysPermission) => {
    setTree(prev => removeNode(prev, n.id));
    toast.success(`已删除「${n.name}」`);
  };

  const save = () => {
    if (!editing?.name || !editing?.code) { toast.error("请填写名称与编码"); return; }
    if (isNew) {
      const node: SysPermission = {
        id: `p-${Date.now()}`,
        parentId: editing.parentId ?? null,
        name: editing.name!, code: editing.code!,
        type: (editing.type as PermissionType) ?? "menu",
        path: editing.path, sort: Number(editing.sort) || 1,
      };
      const next = cloneTree(tree);
      if (!node.parentId) {
        next.push(node);
      } else {
        const parent = findNode(next, node.parentId);
        if (parent) { parent.children = parent.children ?? []; parent.children.push(node); }
      }
      setTree(next);
      toast.success("已新增权限");
    } else {
      const next = cloneTree(tree);
      const target = findNode(next, editing.id!);
      if (target) Object.assign(target, editing);
      setTree(next);
      toast.success("已更新");
    }
    setEditing(null);
  };

  return (
    <div className="page-container">
      <PageHeader title="权限管理" subtitle="管理菜单、按钮、接口三级权限节点" breadcrumbs={[{ label: "系统管理" }, { label: "权限管理" }]}
        actions={<>
          <Button variant="outline" onClick={expandAll}>展开全部</Button>
          <Button variant="outline" onClick={() => setExpanded(new Set())}>折叠全部</Button>
          <Button className="gap-1.5" onClick={() => onAdd(null)}><Plus className="h-4 w-4" />新增根节点</Button>
        </>} />

      <div className="panel p-0 overflow-hidden">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              <TableHead>名称</TableHead>
              <TableHead>权限编码</TableHead>
              <TableHead className="w-20">类型</TableHead>
              <TableHead>路径</TableHead>
              <TableHead className="w-20 text-right">排序</TableHead>
              <TableHead className="w-[180px] text-right">操作</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {tree.map(node => (
              <PermissionRow key={node.id} node={node} level={0} expanded={expanded}
                onToggle={toggle} onAdd={onAdd} onEdit={onEdit} onDelete={onDelete} />
            ))}
          </TableBody>
        </Table>
      </div>

      <Dialog open={!!editing} onOpenChange={v => !v && setEditing(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>{isNew ? "新增权限" : "编辑权限"}</DialogTitle></DialogHeader>
          {editing && (
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5"><Label>名称 <span className="text-destructive">*</span></Label><Input value={editing.name ?? ""} onChange={e => setEditing({ ...editing, name: e.target.value })} /></div>
              <div className="space-y-1.5"><Label>编码 <span className="text-destructive">*</span></Label><Input value={editing.code ?? ""} onChange={e => setEditing({ ...editing, code: e.target.value })} placeholder="例如 inbound:create" /></div>
              <div className="space-y-1.5">
                <Label>类型</Label>
                <Select value={editing.type ?? "menu"} onValueChange={v => setEditing({ ...editing, type: v as PermissionType })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent><SelectItem value="menu">菜单</SelectItem><SelectItem value="button">按钮</SelectItem><SelectItem value="api">接口</SelectItem></SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5"><Label>排序</Label><Input type="number" value={editing.sort ?? 1} onChange={e => setEditing({ ...editing, sort: Number(e.target.value) })} /></div>
              <div className="col-span-2 space-y-1.5"><Label>路径</Label><Input value={editing.path ?? ""} onChange={e => setEditing({ ...editing, path: e.target.value })} placeholder="仅菜单类型需要" /></div>
            </div>
          )}
          <DialogFooter><Button variant="outline" onClick={() => setEditing(null)}>取消</Button><Button onClick={save}>保存</Button></DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
