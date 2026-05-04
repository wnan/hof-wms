import { useEffect, useState } from "react";
import { PageHeader } from "@/components/PageHeader";
import { ArrowDownToLine, ArrowUpFromLine, Boxes, AlertTriangle, TrendingUp, TrendingDown } from "lucide-react";
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend } from "recharts";
import { StatusBadge } from "@/components/StatusBadge";
import { dashboardApi, type DashboardSummary } from "@/api/dashboard";

const COLORS = ["hsl(214 88% 38%)", "hsl(199 89% 48%)", "hsl(142 71% 38%)", "hsl(38 92% 50%)", "hsl(280 70% 55%)"];

function StatCard({ title, value, delta, up, icon: Icon, tone }: { title: string; value: string | number; delta: string; up: boolean; icon: React.ComponentType<{ className?: string }>; tone: string }) {
  return (
    <div className="stat-card">
      <div className="flex items-start justify-between">
        <div className="space-y-2">
          <p className="text-sm text-muted-foreground">{title}</p>
          <p className="text-2xl font-semibold">{value}</p>
          <div className={`flex items-center text-xs gap-1 ${up ? "text-success" : "text-destructive"}`}>
            {up ? <TrendingUp className="h-3 w-3" /> : <TrendingDown className="h-3 w-3" />}
            <span>{delta}</span>
            <span className="text-muted-foreground ml-1">较上周</span>
          </div>
        </div>
        <div className={`h-10 w-10 rounded-md flex items-center justify-center ${tone}`}>
          <Icon className="h-5 w-5" />
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);

  useEffect(() => {
    dashboardApi.summary().then(setSummary).catch(() => undefined);
  }, []);

  const trend = summary?.trend.map((item) => ({
    date: item.date,
    入库: item.inbound,
    出库: item.outbound,
  })) ?? [];
  const categoryDist = summary?.categoryDist ?? [];
  const warehouseLoad = summary?.warehouseLoad.map((item) => ({
    name: item.name,
    吞吐: item.value,
  })) ?? [];
  const latestInbound = summary?.latestInbound ?? [];

  return (
    <div className="page-container">
      <PageHeader title="仪表盘" subtitle="今日运营概览与趋势分析" />

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="今日入库单" value={summary?.metrics.inboundCount ?? 0} delta="+12.5%" up icon={ArrowDownToLine} tone="bg-primary-soft text-primary" />
        <StatCard title="今日出库单" value={summary?.metrics.outboundCount ?? 0} delta="+8.2%" up icon={ArrowUpFromLine} tone="bg-info-soft text-info" />
        <StatCard title="在库 SKU" value={summary?.metrics.inventorySkuCount ?? 0} delta="-2.1%" up={false} icon={Boxes} tone="bg-success-soft text-success" />
        <StatCard title="库存预警" value={summary?.metrics.alertCount ?? 0} delta="+3" up={false} icon={AlertTriangle} tone="bg-warning-soft text-warning" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="panel lg:col-span-2">
          <div className="flex items-center justify-between mb-4">
            <h3 className="font-semibold">近 14 日出入库趋势</h3>
            <span className="text-xs text-muted-foreground">单位：单</span>
          </div>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={trend}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
              <XAxis dataKey="date" stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
              <Legend />
              <Line type="monotone" dataKey="入库" stroke="hsl(214 88% 38%)" strokeWidth={2} dot={{ r: 3 }} />
              <Line type="monotone" dataKey="出库" stroke="hsl(199 89% 48%)" strokeWidth={2} dot={{ r: 3 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
        <div className="panel">
          <h3 className="font-semibold mb-4">商品分类占比</h3>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie data={categoryDist} dataKey="value" nameKey="name" innerRadius={50} outerRadius={90} paddingAngle={2}>
                {categoryDist.map((_, i) => <Cell key={i} fill={COLORS[i]} />)}
              </Pie>
              <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
              <Legend wrapperStyle={{ fontSize: 12 }} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="panel">
          <h3 className="font-semibold mb-4">仓库吞吐量 TOP 4</h3>
          <ResponsiveContainer width="100%" height={240}>
            <BarChart data={warehouseLoad}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
              <XAxis dataKey="name" stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
              <Bar dataKey="吞吐" fill="hsl(214 88% 38%)" radius={[6, 6, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="panel">
          <h3 className="font-semibold mb-4">最新入库单</h3>
          <div className="space-y-3">
            {latestInbound.map(o => (
              <div key={o.id} className="flex items-center justify-between py-2 border-b border-border last:border-0">
                <div className="min-w-0">
                  <p className="text-sm font-medium truncate">{o.code || o.orderNo || '-'}</p>
                  <p className="text-xs text-muted-foreground truncate">{o.supplier || o.supplierName} · ¥{(o.totalAmount || 0).toLocaleString()}</p>
                </div>
                <StatusBadge value={o.status} />
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
