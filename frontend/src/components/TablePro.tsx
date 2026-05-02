import { ReactNode } from "react";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { ChevronLeft, ChevronRight, Inbox } from "lucide-react";
import { cn } from "@/lib/utils";

export interface ColumnConfig<T> {
  key: string;
  title: string;
  width?: string | number;
  align?: "left" | "center" | "right";
  render?: (row: T, index: number) => ReactNode;
}

export interface ActionConfig<T> {
  label: string;
  variant?: "default" | "outline" | "ghost" | "destructive";
  onClick: (row: T) => void;
  show?: (row: T) => boolean;
}

interface Props<T> {
  columns: ColumnConfig<T>[];
  data: T[];
  loading?: boolean;
  rowKey?: (row: T) => string;
  selection?: boolean;
  selected?: string[];
  onSelectionChange?: (ids: string[]) => void;
  actions?: ActionConfig<T>[];
  pagination?: { current: number; size: number; total: number; onChange: (page: number) => void };
  toolbar?: ReactNode;
}

export function TablePro<T>({
  columns, data, loading, rowKey = (r) => String((r as { id?: string }).id ?? ""),
  selection, selected = [], onSelectionChange,
  actions, pagination, toolbar,
}: Props<T>) {
  const allSelected = data.length > 0 && data.every(r => selected.includes(rowKey(r)));
  const toggleAll = (v: boolean) => {
    if (!onSelectionChange) return;
    if (v) onSelectionChange(Array.from(new Set([...selected, ...data.map(rowKey)])));
    else onSelectionChange(selected.filter(id => !data.map(rowKey).includes(id)));
  };
  const toggleRow = (id: string, v: boolean) => {
    if (!onSelectionChange) return;
    onSelectionChange(v ? [...selected, id] : selected.filter(x => x !== id));
  };

  const totalPages = pagination ? Math.max(1, Math.ceil(pagination.total / pagination.size)) : 1;

  return (
    <div className="panel p-0 overflow-hidden">
      {toolbar && <div className="flex items-center justify-between px-5 py-3 border-b border-border">{toolbar}</div>}
      <div className="overflow-x-auto">
        <Table>
          <TableHeader>
            <TableRow className="bg-muted/40 hover:bg-muted/40">
              {selection && (
                <TableHead className="w-10">
                  <Checkbox checked={allSelected} onCheckedChange={v => toggleAll(!!v)} />
                </TableHead>
              )}
              {columns.map(c => (
                <TableHead
                  key={c.key}
                  style={{ width: c.width, textAlign: c.align ?? "left" }}
                  className="font-semibold text-foreground"
                >{c.title}</TableHead>
              ))}
              {actions && <TableHead className="text-right w-[180px]">操作</TableHead>}
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading ? (
              <TableRow><TableCell colSpan={columns.length + (actions ? 1 : 0) + (selection ? 1 : 0)} className="text-center py-12 text-muted-foreground">加载中...</TableCell></TableRow>
            ) : data.length === 0 ? (
              <TableRow><TableCell colSpan={columns.length + (actions ? 1 : 0) + (selection ? 1 : 0)} className="text-center py-16">
                <div className="flex flex-col items-center gap-2 text-muted-foreground">
                  <Inbox className="h-10 w-10 opacity-40" />
                  <span className="text-sm">暂无数据</span>
                </div>
              </TableCell></TableRow>
            ) : data.map((row, i) => {
              const id = rowKey(row);
              return (
                <TableRow key={id || i} className={cn(selected.includes(id) && "bg-primary-soft/40")}>
                  {selection && (
                    <TableCell><Checkbox checked={selected.includes(id)} onCheckedChange={v => toggleRow(id, !!v)} /></TableCell>
                  )}
                  {columns.map(c => (
                    <TableCell key={c.key} style={{ textAlign: c.align ?? "left" }}>
                      {c.render ? c.render(row, i) : String((row as Record<string, unknown>)[c.key] ?? "—")}
                    </TableCell>
                  ))}
                  {actions && (
                    <TableCell className="text-right">
                      <div className="flex justify-end gap-1">
                        {actions.filter(a => !a.show || a.show(row)).map(a => (
                          <Button key={a.label} size="sm" variant={a.variant ?? "ghost"} onClick={() => a.onClick(row)}>
                            {a.label}
                          </Button>
                        ))}
                      </div>
                    </TableCell>
                  )}
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </div>

      {pagination && (
        <div className="flex items-center justify-between px-5 py-3 border-t border-border text-sm">
          <span className="text-muted-foreground">共 <span className="text-foreground font-medium">{pagination.total}</span> 条</span>
          <div className="flex items-center gap-2">
            <Button variant="outline" size="sm" disabled={pagination.current <= 1} onClick={() => pagination.onChange(pagination.current - 1)}>
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <span className="px-3 text-muted-foreground">{pagination.current} / {totalPages}</span>
            <Button variant="outline" size="sm" disabled={pagination.current >= totalPages} onClick={() => pagination.onChange(pagination.current + 1)}>
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
