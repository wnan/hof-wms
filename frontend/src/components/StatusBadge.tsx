import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

type Tone = "default" | "info" | "success" | "warning" | "destructive" | "muted";

const toneClass: Record<Tone, string> = {
  default: "bg-primary-soft text-primary border-transparent",
  info: "bg-info-soft text-info border-transparent",
  success: "bg-success-soft text-success border-transparent",
  warning: "bg-warning-soft text-warning border-transparent",
  destructive: "bg-destructive/10 text-destructive border-transparent",
  muted: "bg-muted text-muted-foreground border-transparent",
};

const map: Record<string, { label: string; tone: Tone }> = {
  // 入库
  draft: { label: "草稿", tone: "muted" },
  pending: { label: "待审核", tone: "warning" },
  approved: { label: "已审核", tone: "info" },
  rejected: { label: "已驳回", tone: "destructive" },
  stored: { label: "已入库", tone: "success" },
  // 出库
  shipped: { label: "已出库", tone: "success" },
  // 同步
  idle: { label: "待运行", tone: "muted" },
  running: { label: "运行中", tone: "info" },
  success: { label: "成功", tone: "success" },
  failed: { label: "失败", tone: "destructive" },
  // 库存
  normal: { label: "正常", tone: "success" },
  low: { label: "库存不足", tone: "warning" },
  out: { label: "已售罄", tone: "destructive" },
};

export function StatusBadge({ value, className }: { value: string; className?: string }) {
  const m = map[value] ?? { label: value, tone: "default" as Tone };
  return (
    <Badge variant="outline" className={cn("font-medium", toneClass[m.tone], className)}>
      <span className="inline-block w-1.5 h-1.5 rounded-full bg-current mr-1.5 opacity-80" />
      {m.label}
    </Badge>
  );
}
