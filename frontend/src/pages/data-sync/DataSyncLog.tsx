import { useMemo, useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { SearchBar, type SearchField } from "@/components/SearchBar";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { StatusBadge } from "@/components/StatusBadge";
import { mockSyncLogs, paginate } from "@/mock/data";
import type { SyncLog } from "@/types/data-sync";

const fields: SearchField[] = [
  { name: "taskName", label: "任务名称", type: "input" },
  { name: "status", label: "执行状态", type: "select", options: [
    { label: "成功", value: "success" }, { label: "失败", value: "failed" }, { label: "运行中", value: "running" },
  ] },
  { name: "startDate", label: "开始日期", type: "date" },
];

export default function DataSyncLog() {
  const [filters, setFilters] = useState<Record<string, string>>({});
  const [query, setQuery] = useState<Record<string, string>>({});
  const [page, setPage] = useState(1);
  const filtered = useMemo(() => mockSyncLogs.filter(l => {
    if (query.taskName && !l.taskName.includes(query.taskName)) return false;
    if (query.status && l.status !== query.status) return false;
    return true;
  }), [query]);
  const data = paginate(filtered, page, 12);

  const columns: ColumnConfig<SyncLog>[] = [
    { key: "taskName", title: "任务名称" },
    { key: "startAt", title: "开始时间", render: r => <span className="text-muted-foreground text-sm">{r.startAt}</span> },
    { key: "endAt", title: "结束时间", render: r => <span className="text-muted-foreground text-sm">{r.endAt}</span> },
    { key: "count", title: "同步数量", align: "right", render: r => r.count.toLocaleString() },
    { key: "status", title: "状态", render: r => <StatusBadge value={r.status} /> },
    { key: "error", title: "错误信息", render: r => r.error ? <span className="text-destructive text-sm">{r.error}</span> : <span className="text-muted-foreground">—</span> },
  ];

  return (
    <div className="page-container">
      <PageHeader title="同步日志" subtitle="查看同步任务的执行记录" breadcrumbs={[{ label: "数据对接" }, { label: "同步日志" }]} />
      <SearchBar fields={fields} values={filters} onChange={setFilters} onSearch={() => { setQuery(filters); setPage(1); }} />
      <TablePro columns={columns} data={data.records} rowKey={r => r.id}
        pagination={{ current: data.current, size: data.size, total: data.total, onChange: setPage }} />
    </div>
  );
}
