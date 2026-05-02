import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ArrowDownToLine, ArrowUpFromLine, Boxes, BarChart3, ChevronRight } from "lucide-react";
import type { ReportCardMeta } from "@/types/report";

const cards: ReportCardMeta[] = [
  { type: "inbound-summary", title: "入库汇总报表", description: "按供应商、时间维度汇总入库数据", category: "inbound" },
  { type: "inbound-trend", title: "入库趋势分析", description: "入库金额与数量变化趋势", category: "inbound" },
  { type: "outbound-summary", title: "出库汇总报表", description: "按客户、产品维度统计出库", category: "outbound" },
  { type: "outbound-rank", title: "销售排行榜", description: "TOP 客户与 TOP SKU", category: "outbound" },
  { type: "stock-snapshot", title: "库存快照", description: "各仓库实时库存分布", category: "inventory" },
  { type: "stock-turnover", title: "库存周转率", description: "周转天数与周转次数分析", category: "inventory" },
  { type: "comprehensive", title: "经营综合报表", description: "入库 / 出库 / 库存综合分析", category: "comprehensive" },
  { type: "warehouse-load", title: "仓库利用率", description: "各仓库容量利用情况", category: "comprehensive" },
];

const iconMap = { inbound: ArrowDownToLine, outbound: ArrowUpFromLine, inventory: Boxes, comprehensive: BarChart3 };
const toneMap = { inbound: "bg-primary-soft text-primary", outbound: "bg-info-soft text-info", inventory: "bg-success-soft text-success", comprehensive: "bg-warning-soft text-warning" };

export default function ReportCenter() {
  const nav = useNavigate();
  const [tab, setTab] = useState<"all" | ReportCardMeta["category"]>("all");
  const list = tab === "all" ? cards : cards.filter(c => c.category === tab);

  return (
    <div className="page-container">
      <PageHeader title="报表中心" subtitle="选择需要查看的报表类型" breadcrumbs={[{ label: "报表分析" }, { label: "报表中心" }]} />
      <Tabs value={tab} onValueChange={v => setTab(v as typeof tab)}>
        <TabsList>
          <TabsTrigger value="all">全部</TabsTrigger>
          <TabsTrigger value="inbound">入库报表</TabsTrigger>
          <TabsTrigger value="outbound">出库报表</TabsTrigger>
          <TabsTrigger value="inventory">库存报表</TabsTrigger>
          <TabsTrigger value="comprehensive">综合报表</TabsTrigger>
        </TabsList>
        <TabsContent value={tab} className="mt-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
            {list.map(c => {
              const Icon = iconMap[c.category];
              return (
                <button key={c.type} onClick={() => nav(`/report/detail/${c.type}`)}
                  className="stat-card text-left group hover:border-primary/40">
                  <div className="flex items-start justify-between mb-3">
                    <div className={`h-10 w-10 rounded-md flex items-center justify-center ${toneMap[c.category]}`}><Icon className="h-5 w-5" /></div>
                    <ChevronRight className="h-4 w-4 text-muted-foreground group-hover:text-primary group-hover:translate-x-0.5 transition" />
                  </div>
                  <h3 className="font-semibold text-sm mb-1">{c.title}</h3>
                  <p className="text-xs text-muted-foreground line-clamp-2">{c.description}</p>
                </button>
              );
            })}
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}
