import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { ExportButton } from "@/components/ExportButton";
import { TablePro, type ColumnConfig } from "@/components/TablePro";
import { ArrowLeft } from "lucide-react";
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from "recharts";
import { reportApi } from "@/api/report";

type Row = { id: string; warehouse: string; inboundAmount: number; outboundAmount: number; inventoryAmount: number };

export default function ReportDetail() {
  const { type } = useParams();
  const nav = useNavigate();
  const [start, setStart] = useState("2026-01-01");
  const [end, setEnd] = useState("2026-04-29");
  const [warehouse, setWarehouse] = useState("__all__");
  const [chartData, setChartData] = useState<Array<{ 月份: string; 入库: number; 出库: number; 库存: number }>>([]);
  const [tableData, setTableData] = useState<Row[]>([]);

  const load = () => {
    if (!type) return;
    reportApi.detail(type, { startDate: start, endDate: end, warehouse }).then((res) => {
      const trend = ((res as unknown as { trend?: Array<{ month: string; inbound: number; outbound: number; inventory: number }> }).trend) ?? [];
      setChartData(trend.map((item) => ({ 月份: item.month, 入库: item.inbound, 出库: item.outbound, 库存: item.inventory })));
      setTableData((res.table as Row[]) ?? []);
    }).catch(() => undefined);
  };

  useEffect(() => {
    load();
  }, [type]);

  const cols: ColumnConfig<Row>[] = [
    { key: "warehouse", title: "仓库" },
    { key: "inboundAmount", title: "入库金额", align: "right", render: r => `¥${r.inboundAmount.toLocaleString()}` },
    { key: "outboundAmount", title: "出库金额", align: "right", render: r => `¥${r.outboundAmount.toLocaleString()}` },
    { key: "inventoryAmount", title: "库存金额", align: "right", render: r => `¥${r.inventoryAmount.toLocaleString()}` },
  ];

  return (
    <div className="page-container">
      <PageHeader title={`报表详情 · ${type}`} breadcrumbs={[{ label: "报表分析", to: "/report/center" }, { label: "报表中心", to: "/report/center" }, { label: "详情" }]}
        actions={<><Button variant="outline" onClick={() => nav("/report/center")} className="gap-1.5"><ArrowLeft className="h-4 w-4" />返回</Button><ExportButton /></>} />

      <div className="panel">
        <div className="flex flex-wrap items-end gap-3">
          <div className="space-y-1.5"><Label className="text-xs">开始日期</Label><Input type="date" value={start} onChange={e => setStart(e.target.value)} /></div>
          <div className="space-y-1.5"><Label className="text-xs">结束日期</Label><Input type="date" value={end} onChange={e => setEnd(e.target.value)} /></div>
          <div className="space-y-1.5 min-w-[180px]">
            <Label className="text-xs">仓库</Label>
            <Select value={warehouse} onValueChange={setWarehouse}>
              <SelectTrigger><SelectValue /></SelectTrigger>
              <SelectContent>
                <SelectItem value="__all__">全部仓库</SelectItem>
                <SelectItem value="sh">上海中心仓</SelectItem><SelectItem value="gz">广州前置仓</SelectItem>
              </SelectContent>
            </Select>
          </div>
          <Button onClick={load}>查询</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="panel">
          <h3 className="font-semibold mb-4">出入库趋势</h3>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
              <XAxis dataKey="月份" stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
              <Legend />
              <Bar dataKey="入库" fill="hsl(214 88% 38%)" radius={[4, 4, 0, 0]} />
              <Bar dataKey="出库" fill="hsl(199 89% 48%)" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
        <div className="panel">
          <h3 className="font-semibold mb-4">库存走势</h3>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
              <XAxis dataKey="月份" stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
              <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
              <Line type="monotone" dataKey="库存" stroke="hsl(142 71% 38%)" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div>
        <h3 className="font-semibold mb-3">明细数据</h3>
        <TablePro columns={cols} data={tableData} rowKey={r => r.id} />
      </div>
    </div>
  );
}
