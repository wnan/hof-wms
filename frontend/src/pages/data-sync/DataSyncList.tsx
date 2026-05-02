import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { Button } from "@/components/ui/button";
import { Plus, Play } from "lucide-react";
import { mockSyncTasks, paginate } from "@/mock/data";
import type { SyncTask } from "@/types/data-sync";
import { toast } from "sonner";

const fields: SearchField[] = [
  { name: "name", label: "任务名称", type: "input" },
  { name: "externalSystem", label: "外部系统", type: "input" },
  { name: "status", label: "状态", type: "select", options: [
    { label: "待运行", value: "idle" }, { label: "运行中", value: "running" },
    { label: "成功", value: "success" }, { label: "失败", value: "failed" },
  ] },
];

export default function DataSyncList() {
  const nav = useNavigate();
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const filtered = useMemo(() => mockSyncTasks.filter(t => {
    if (query.name && !t.name.includes(query.name)) return false;
    if (query.externalSystem && !t.externalSystem.includes(query.externalSystem)) return false;
    if (query.status && t.status !== query.status) return false;
    return true;
  }), [query]);
  const data = paginate(filtered, page, 10);

  const columns: ColumnConfig<SyncTask>[] = [
    { key: "name", title: "任务名称", render: r => <span className="font-medium">{r.name}</span> },
    { key: "externalSystem", title: "外部系统" },
    { key: "syncType", title: "同步类型", render: r => r.syncType === "incremental" ? "增量" : "全量" },
    { key: "triggerType", title: "触发方式", render: r => r.triggerType === "schedule" ? `定时 (${r.cron ?? "—"})` : "手动" },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status} /> },
    { key: "lastRunAt", title: "上次执行", render: r => <span className="text-muted-foreground text-sm">{r.lastRunAt ?? "—"}</span> },
  ];
  return (
    <div className="page-container">
      <PageHeader title="同步任务" subtitle="管理外部系统数据对接任务" breadcrumbs={[{ label: "数据对接" }, { label: "同步任务" }]}
        actions={<Button className="gap-1.5" onClick={() => nav("/data-sync/config/new")}><Plus className="h-4 w-4" />新建同步任务</Button>} />
      <SearchBar fields={fields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro columns={columns} data={data.records} rowKey={r => r.id}
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }}
        actions={[
          { label: "执行", variant: "ghost", onClick: () => toast.success("已触发执行") },
          { label: "配置", onClick: r => nav(`/data-sync/config/${r.id}`) },
          { label: "日志", variant: "ghost", onClick: () => nav("/data-sync/log") },
        ]}
      />
    </div>
  );
}
