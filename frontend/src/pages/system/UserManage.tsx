import { useMemo, useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Plus, KeyRound } from "lucide-react";
import { mockUsers, mockRoles } from "@/mock/rbac";
import type { SysUser } from "@/types/rbac";
import { toast } from "sonner";

const fields: SearchField[] = [
  { name: "username", label: "用户名", type: "input" },
  { name: "realName", label: "姓名", type: "input" },
  { name: "department", label: "部门", type: "select", options: [
    { label: "技术部", value: "技术部" }, { label: "运营中心", value: "运营中心" },
    { label: "供应链部", value: "供应链部" }, { label: "财务部", value: "财务部" }, { label: "客户服务部", value: "客户服务部" },
  ] },
  { name: "status", label: "状态", type: "select", options: [
    { label: "启用", value: "active" }, { label: "禁用", value: "disabled" },
  ] },
];

function paginate<T>(rows: T[], current: number, size: number) {
  const start = (current - 1) * size;
  return { records: rows.slice(start, start + size), total: rows.length, current, size };
}

const empty: Partial<SysUser> = { username: "", realName: "", email: "", phone: "", department: "技术部", roleIds: [], status: "active" };

export default function UserManage() {
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const [users, setUsers] = useState<SysUser[]>(mockUsers);
  const [editing, setEditing] = useState<Partial<SysUser> | null>(null);

  const filtered = useMemo(() => users.filter(u => {
    if (query.username && !u.username.includes(query.username)) return false;
    if (query.realName && !u.realName.includes(query.realName)) return false;
    if (query.department && u.department !== query.department) return false;
    if (query.status && u.status !== query.status) return false;
    return true;
  }), [query, users]);
  const data = paginate(filtered, page, 10);

  const toggleStatus = (u: SysUser) => {
    setUsers(prev => prev.map(x => x.id === u.id ? { ...x, status: x.status === "active" ? "disabled" : "active" } : x));
    toast.success(`已${u.status === "active" ? "禁用" : "启用"}用户 ${u.realName}`);
  };

  const save = () => {
    if (!editing?.username || !editing?.realName) { toast.error("请填写用户名与姓名"); return; }
    if (editing.id) {
      setUsers(prev => prev.map(x => x.id === editing.id ? { ...x, ...editing } as SysUser : x));
      toast.success("已更新");
    } else {
      const id = `u${Date.now()}`;
      setUsers(prev => [{ ...empty, ...editing, id, createdAt: new Date().toISOString().slice(0, 19).replace("T", " ") } as SysUser, ...prev]);
      toast.success("已新增");
    }
    setEditing(null);
  };

  const columns: ColumnConfig<SysUser>[] = [
    { key: "username", title: "用户", render: r => (
      <div className="flex items-center gap-2.5">
        <Avatar className="h-8 w-8"><AvatarFallback className="bg-primary-soft text-primary text-xs">{r.realName.slice(0, 1)}</AvatarFallback></Avatar>
        <div className="leading-tight">
          <div className="text-sm font-medium">{r.realName}</div>
          <div className="text-xs text-muted-foreground">@{r.username}</div>
        </div>
      </div>
    ) },
    { key: "department", title: "部门" },
    { key: "email", title: "联系方式", render: r => (<div className="text-xs leading-relaxed"><div>{r.email}</div><div className="text-muted-foreground">{r.phone}</div></div>) },
    { key: "roleIds", title: "角色", render: r => (
      <div className="flex flex-wrap gap-1">
        {r.roleIds.map(id => {
          const role = mockRoles.find(x => x.id === id);
          return <Badge key={id} variant="outline" className="bg-accent text-accent-foreground border-transparent">{role?.name ?? id}</Badge>;
        })}
      </div>
    ) },
    { key: "lastLoginAt", title: "最近登录", render: r => <span className="text-muted-foreground text-xs">{r.lastLoginAt}</span> },
    { key: "status", title: "状态", render: r => (
      <div className="flex items-center gap-2">
        <Switch checked={r.status === "active"} onCheckedChange={() => toggleStatus(r)} />
        <StatusBadge value={r.status === "active" ? "success" : "muted"} />
      </div>
    ) },
  ];

  return (
    <div className="page-container">
      <PageHeader title="用户管理" subtitle="管理系统用户、分配角色与控制访问权限" breadcrumbs={[{ label: "系统管理" }, { label: "用户管理" }]}
        actions={<Button className="gap-1.5" onClick={() => setEditing({ ...empty })}><Plus className="h-4 w-4" />新增用户</Button>} />
      <SearchBar fields={fields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro columns={columns} data={data.records} rowKey={r => r.id}
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }}
        actions={[
          { label: "编辑", onClick: r => setEditing(r) },
          { label: "重置密码", variant: "ghost", onClick: r => toast.success(`已为 ${r.realName} 重置密码：Wms@2026`) },
          { label: "删除", onClick: r => { setUsers(prev => prev.filter(x => x.id !== r.id)); toast.success("已删除"); } },
        ]}
      />

      <Dialog open={!!editing} onOpenChange={v => !v && setEditing(null)}>
        <DialogContent className="max-w-xl">
          <DialogHeader><DialogTitle className="flex items-center gap-2"><KeyRound className="h-4 w-4 text-primary" />{editing?.id ? "编辑用户" : "新增用户"}</DialogTitle></DialogHeader>
          {editing && (
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5"><Label>用户名 <span className="text-destructive">*</span></Label><Input value={editing.username ?? ""} onChange={e => setEditing({ ...editing, username: e.target.value })} disabled={!!editing.id} /></div>
              <div className="space-y-1.5"><Label>姓名 <span className="text-destructive">*</span></Label><Input value={editing.realName ?? ""} onChange={e => setEditing({ ...editing, realName: e.target.value })} /></div>
              <div className="space-y-1.5"><Label>邮箱</Label><Input type="email" value={editing.email ?? ""} onChange={e => setEditing({ ...editing, email: e.target.value })} /></div>
              <div className="space-y-1.5"><Label>手机号</Label><Input value={editing.phone ?? ""} onChange={e => setEditing({ ...editing, phone: e.target.value })} /></div>
              <div className="space-y-1.5">
                <Label>部门</Label>
                <Select value={editing.department ?? "技术部"} onValueChange={v => setEditing({ ...editing, department: v })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {["技术部","运营中心","供应链部","财务部","客户服务部"].map(d => <SelectItem key={d} value={d}>{d}</SelectItem>)}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>状态</Label>
                <Select value={editing.status ?? "active"} onValueChange={v => setEditing({ ...editing, status: v as SysUser["status"] })}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent><SelectItem value="active">启用</SelectItem><SelectItem value="disabled">禁用</SelectItem></SelectContent>
                </Select>
              </div>
              <div className="col-span-2 space-y-1.5">
                <Label>分配角色（可多选）</Label>
                <div className="flex flex-wrap gap-2 p-3 rounded-md border border-border bg-muted/30 min-h-[60px]">
                  {mockRoles.map(role => {
                    const checked = (editing.roleIds ?? []).includes(role.id);
                    return (
                      <button key={role.id} type="button"
                        onClick={() => {
                          const ids = editing.roleIds ?? [];
                          setEditing({ ...editing, roleIds: checked ? ids.filter(x => x !== role.id) : [...ids, role.id] });
                        }}
                        className={`px-3 py-1.5 rounded-md text-xs border transition ${checked ? "bg-primary text-primary-foreground border-primary" : "bg-card border-border hover:border-primary/40"}`}>
                        {role.name}
                      </button>
                    );
                  })}
                </div>
              </div>
            </div>
          )}
          <DialogFooter>
            <Button variant="outline" onClick={() => setEditing(null)}>取消</Button>
            <Button onClick={save}>保存</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
