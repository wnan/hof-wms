import { useMemo, useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Checkbox } from "@/components/ui/checkbox";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Sheet, SheetContent, SheetFooter, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Plus, ShieldCheck, ChevronRight } from "lucide-react";
import { mockRoles, mockPermissions } from "@/mock/rbac";
import type { SysRole, SysPermission } from "@/types/rbac";
import { toast } from "sonner";

function flatten(nodes: SysPermission[]): SysPermission[] {
  return nodes.flatMap(n => [n, ...(n.children ? flatten(n.children) : [])]);
}
const allPermissionIds = flatten(mockPermissions).map(p => p.id);

function PermissionTree({ nodes, checked, onChange, level = 0 }: {
  nodes: SysPermission[]; checked: Set<string>; onChange: (s: Set<string>) => void; level?: number;
}) {
  const toggle = (node: SysPermission, v: boolean) => {
    const next = new Set(checked);
    const ids = flatten([node]).map(n => n.id);
    if (v) ids.forEach(id => next.add(id));
    else ids.forEach(id => next.delete(id));
    onChange(next);
  };
  return (
    <div className="space-y-1">
      {nodes.map(n => {
        const desc = flatten([n]).map(x => x.id);
        const allChecked = desc.every(id => checked.has(id));
        const someChecked = !allChecked && desc.some(id => checked.has(id));
        return (
          <div key={n.id}>
            <div className="flex items-center gap-2 py-1.5 px-2 rounded hover:bg-muted/50" style={{ paddingLeft: 8 + level * 20 }}>
              {n.children && n.children.length > 0
                ? <ChevronRight className="h-3.5 w-3.5 text-muted-foreground" />
                : <span className="w-3.5" />}
              <Checkbox
                checked={allChecked ? true : someChecked ? "indeterminate" : false}
                onCheckedChange={v => toggle(n, !!v)}
              />
              <span className="text-sm">{n.name}</span>
              <span className="text-xs text-muted-foreground font-mono">{n.code}</span>
              <span className={`text-[10px] px-1.5 py-0.5 rounded ${
                n.type === "menu" ? "bg-primary-soft text-primary" :
                n.type === "button" ? "bg-info-soft text-info" : "bg-warning-soft text-warning"
              }`}>{n.type === "menu" ? "菜单" : n.type === "button" ? "按钮" : "接口"}</span>
            </div>
            {n.children && n.children.length > 0 && (
              <PermissionTree nodes={n.children} checked={checked} onChange={onChange} level={level + 1} />
            )}
          </div>
        );
      })}
    </div>
  );
}

const empty: Partial<SysRole> = { code: "", name: "", description: "", permissionIds: [], status: "active" };

export default function RoleManage() {
  const [roles, setRoles] = useState<SysRole[]>(mockRoles);
  const [editing, setEditing] = useState<Partial<SysRole> | null>(null);
  const [assigning, setAssigning] = useState<SysRole | null>(null);
  const [checked, setChecked] = useState<Set<string>>(new Set());

  const columns: ColumnConfig<SysRole>[] = [
    { key: "name", title: "角色名称", render: r => (
      <div className="flex items-center gap-2.5">
        <div className="h-8 w-8 rounded-md bg-primary-soft text-primary flex items-center justify-center"><ShieldCheck className="h-4 w-4" /></div>
        <div className="leading-tight"><div className="text-sm font-medium">{r.name}</div><div className="text-xs text-muted-foreground font-mono">{r.code}</div></div>
      </div>
    ) },
    { key: "description", title: "描述", render: r => <span className="text-sm text-muted-foreground">{r.description}</span> },
    { key: "userCount", title: "用户数", align: "right", render: r => <span className="font-medium">{r.userCount}</span> },
    { key: "permissionIds", title: "权限数", align: "right", render: r => r.permissionIds.includes("*") ? <span className="text-primary font-medium">全部</span> : r.permissionIds.length },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status === "active" ? "success" : "muted"} /> },
    { key: "createdAt", title: "创建时间", render: r => <span className="text-muted-foreground text-xs">{r.createdAt}</span> },
  ];

  const openAssign = (r: SysRole) => {
    setAssigning(r);
    setChecked(new Set(r.permissionIds.includes("*") ? allPermissionIds : r.permissionIds));
  };
  const saveAssign = () => {
    if (!assigning) return;
    setRoles(prev => prev.map(x => x.id === assigning.id ? { ...x, permissionIds: Array.from(checked) } : x));
    toast.success(`角色「${assigning.name}」权限已更新（共 ${checked.size} 项）`);
    setAssigning(null);
  };
  const save = () => {
    if (!editing?.code || !editing?.name) { toast.error("请填写编码与名称"); return; }
    if (editing.id) {
      setRoles(prev => prev.map(x => x.id === editing.id ? { ...x, ...editing } as SysRole : x));
    } else {
      setRoles(prev => [{ ...empty, ...editing, id: `r${Date.now()}`, userCount: 0, createdAt: new Date().toISOString().slice(0, 19).replace("T", " ") } as SysRole, ...prev]);
    }
    toast.success("已保存");
    setEditing(null);
  };

  return (
    <div className="page-container">
      <PageHeader title="角色管理" subtitle="定义角色并为其分配菜单与按钮权限" breadcrumbs={[{ label: "系统管理" }, { label: "角色管理" }]}
        actions={<Button className="gap-1.5" onClick={() => setEditing({ ...empty })}><Plus className="h-4 w-4" />新增角色</Button>} />
      <TablePro columns={columns} data={roles} rowKey={r => r.id}
        actions={[
          { label: "分配权限", onClick: openAssign },
          { label: "编辑", variant: "ghost", onClick: r => setEditing(r) },
          { label: "删除", onClick: r => { setRoles(prev => prev.filter(x => x.id !== r.id)); toast.success("已删除"); } },
        ]}
      />

      {/* 编辑角色 */}
      <Dialog open={!!editing} onOpenChange={v => !v && setEditing(null)}>
        <DialogContent>
          <DialogHeader><DialogTitle>{editing?.id ? "编辑角色" : "新增角色"}</DialogTitle></DialogHeader>
          {editing && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-1.5"><Label>角色编码 <span className="text-destructive">*</span></Label><Input value={editing.code ?? ""} onChange={e => setEditing({ ...editing, code: e.target.value })} placeholder="例如 warehouse_admin" disabled={!!editing.id} /></div>
                <div className="space-y-1.5"><Label>角色名称 <span className="text-destructive">*</span></Label><Input value={editing.name ?? ""} onChange={e => setEditing({ ...editing, name: e.target.value })} /></div>
              </div>
              <div className="space-y-1.5"><Label>描述</Label><Textarea value={editing.description ?? ""} onChange={e => setEditing({ ...editing, description: e.target.value })} rows={3} /></div>
            </div>
          )}
          <DialogFooter><Button variant="outline" onClick={() => setEditing(null)}>取消</Button><Button onClick={save}>保存</Button></DialogFooter>
        </DialogContent>
      </Dialog>

      {/* 分配权限 */}
      <Sheet open={!!assigning} onOpenChange={v => !v && setAssigning(null)}>
        <SheetContent className="sm:max-w-xl w-full flex flex-col">
          <SheetHeader><SheetTitle>分配权限 · {assigning?.name}</SheetTitle></SheetHeader>
          <div className="flex items-center justify-between py-3 border-b border-border">
            <span className="text-sm text-muted-foreground">已选 <span className="font-medium text-foreground">{checked.size}</span> / {allPermissionIds.length}</span>
            <div className="flex gap-2">
              <Button size="sm" variant="outline" onClick={() => setChecked(new Set(allPermissionIds))}>全选</Button>
              <Button size="sm" variant="outline" onClick={() => setChecked(new Set())}>清空</Button>
            </div>
          </div>
          <div className="flex-1 overflow-y-auto py-3">
            <PermissionTree nodes={mockPermissions} checked={checked} onChange={setChecked} />
          </div>
          <SheetFooter><Button variant="outline" onClick={() => setAssigning(null)}>取消</Button><Button onClick={saveAssign}>保存权限</Button></SheetFooter>
        </SheetContent>
      </Sheet>
    </div>
  );
}
