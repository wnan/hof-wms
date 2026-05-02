import { useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { ArrowLeft, Plus, Trash2, Save, Play, Plug } from "lucide-react";
import { mockSyncTasks } from "@/mock/data";
import type { FieldMapping, SyncTask } from "@/types/data-sync";
import { toast } from "sonner";

export default function DataSyncConfig() {
  const { id } = useParams();
  const nav = useNavigate();
  const isNew = !id || id === "new";
  const init = useMemo<SyncTask>(() => isNew
    ? { id: "", name: "", externalSystem: "", endpoint: "", authType: "bearer", syncType: "incremental", triggerType: "schedule", cron: "0 0 * * * ?", mappings: [], status: "idle" }
    : (mockSyncTasks.find(x => x.id === id) ?? mockSyncTasks[0]), [id, isNew]);
  const [form, setForm] = useState<SyncTask>(init);
  const set = <K extends keyof SyncTask>(k: K, v: SyncTask[K]) => setForm(p => ({ ...p, [k]: v }));
  const setMap = (i: number, patch: Partial<FieldMapping>) => {
    const next = [...form.mappings]; next[i] = { ...next[i], ...patch }; set("mappings", next);
  };

  return (
    <div className="page-container">
      <PageHeader title={isNew ? "新建同步任务" : `配置任务：${form.name}`}
        breadcrumbs={[{ label: "数据对接", to: "/data-sync/list" }, { label: "同步任务", to: "/data-sync/list" }, { label: isNew ? "新建" : "配置" }]}
        actions={<Button variant="outline" onClick={() => nav("/data-sync/list")} className="gap-1.5"><ArrowLeft className="h-4 w-4" />返回</Button>} />

      <div className="panel">
        <h3 className="font-semibold mb-4">基本信息</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div className="space-y-1.5"><Label>任务名称 <span className="text-destructive">*</span></Label><Input value={form.name} onChange={e => set("name", e.target.value)} placeholder="例如 ERP 商品同步" /></div>
          <div className="space-y-1.5"><Label>外部系统</Label><Input value={form.externalSystem} onChange={e => set("externalSystem", e.target.value)} placeholder="例如 金蝶 ERP" /></div>
          <div className="space-y-1.5 md:col-span-2"><Label>接口地址</Label><Input value={form.endpoint} onChange={e => set("endpoint", e.target.value)} placeholder="https://..." /></div>
          <div className="space-y-1.5">
            <Label>认证方式</Label>
            <Select value={form.authType} onValueChange={v => set("authType", v as SyncTask["authType"])}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="none">无</SelectItem><SelectItem value="basic">Basic</SelectItem>
                <SelectItem value="bearer">Bearer Token</SelectItem><SelectItem value="apikey">API Key</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </div>

      <div className="panel">
        <h3 className="font-semibold mb-4">同步配置</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="space-y-1.5">
            <Label>同步类型</Label>
            <Select value={form.syncType} onValueChange={v => set("syncType", v as SyncTask["syncType"])}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent><SelectItem value="incremental">增量同步</SelectItem><SelectItem value="full">全量同步</SelectItem></SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label>触发方式</Label>
            <Select value={form.triggerType} onValueChange={v => set("triggerType", v as SyncTask["triggerType"])}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent><SelectItem value="manual">手动</SelectItem><SelectItem value="schedule">定时</SelectItem></SelectContent>
            </Select>
          </div>
          <div className="space-y-1.5">
            <Label>Cron 表达式</Label>
            <Input value={form.cron ?? ""} onChange={e => set("cron", e.target.value)} disabled={form.triggerType !== "schedule"} placeholder="0 0 * * * ?" />
          </div>
        </div>
      </div>

      <div className="panel">
        <div className="flex items-center justify-between mb-4">
          <div><h3 className="font-semibold">字段映射</h3><p className="text-xs text-muted-foreground mt-0.5">将外部系统字段映射到本地字段</p></div>
          <Button size="sm" variant="outline" onClick={() => set("mappings", [...form.mappings, { source: "", target: "" }])} className="gap-1.5"><Plus className="h-4 w-4" />添加映射</Button>
        </div>
        <Table>
          <TableHeader><TableRow className="bg-muted/40 hover:bg-muted/40">
            <TableHead className="w-12">#</TableHead><TableHead>源字段</TableHead><TableHead>目标字段</TableHead><TableHead className="w-16 text-right">操作</TableHead>
          </TableRow></TableHeader>
          <TableBody>
            {form.mappings.length === 0 && <TableRow><TableCell colSpan={4} className="text-center text-muted-foreground text-sm py-8">暂无映射</TableCell></TableRow>}
            {form.mappings.map((m, i) => (
              <TableRow key={i}>
                <TableCell className="text-muted-foreground">{i + 1}</TableCell>
                <TableCell><Input value={m.source} onChange={e => setMap(i, { source: e.target.value })} placeholder="例如 sku_id" /></TableCell>
                <TableCell><Input value={m.target} onChange={e => setMap(i, { target: e.target.value })} placeholder="例如 skuCode" /></TableCell>
                <TableCell className="text-right"><Button size="icon" variant="ghost" onClick={() => set("mappings", form.mappings.filter((_, idx) => idx !== i))}><Trash2 className="h-4 w-4 text-destructive" /></Button></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      <div className="panel flex items-center justify-end gap-2">
        <Button variant="outline" onClick={() => toast.success("连接成功")} className="gap-1.5"><Plug className="h-4 w-4" />测试连接</Button>
        <Button variant="outline" onClick={() => toast.success("已触发执行")} className="gap-1.5"><Play className="h-4 w-4" />手动执行</Button>
        <Button onClick={() => toast.success("已保存")} className="gap-1.5"><Save className="h-4 w-4" />保存</Button>
      </div>
    </div>
  );
}
