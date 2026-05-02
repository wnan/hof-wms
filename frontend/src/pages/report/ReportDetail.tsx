import { useState } from "react";
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

const data = Array.from({ length: 12 }, (_, i) => ({ 月份: `${i + 1}月`, 入库: 200 + ((i * 41) % 200), 出库: 150 + ((i * 53) % 220), 库存: 800 + ((i * 71) % 300) }));
type Row = { id: string; 仓库: string; 入库金额: number; 出库金额: number; 库存金额: number };
const tableData: Row[] = [
  { id: "1", 仓库: "上海中心仓", 入库金额: 1280000, 出库金额: 980000, 库存金额: 3200000 },
  { id: "2", 仓库: "广州前置仓", 入库金额: 860000, 出库金额: 720000, 库存金额: 2100000 },
  { id: "3", 仓库: "成都分拣仓", 入库金额: 540000, 出库金额: 460000, 库存金额: 1380000 },
  { id: "4", 仓库: "北京旗舰仓", 入库金额: 720000, 出库金额: 610000, 库存金额: 1820000 },
];

export default function ReportDetail() {
  const { type } = useParams();
  const nav = useNavigate();
  const [start, setStart] = useState("2026-01-01");
  const [end, setEnd] = useState("2026-04-29");
  const [warehouse, setWarehouse] = useState("__all__");

  const cols: ColumnConfig<Row>[] = [
    { key: "仓库", title: "仓库" },
    { key: "入库金额", title: "入库金额", align: "right", render: r => `¥${r.入库金额.toLocaleString()}` },
    { key: "出库金额", title: "出库金额", align: "right", render: r => `¥${r.出库金额.toLocaleString()}` },
    { key: "库存金额", title: "库存金额", align: "right", render: r => `¥${r.库存金额.toLocaleString()}` },
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
          <Button>查询</Button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <div className="panel">
          <h3 className="font-semibold mb-4">出入库趋势</h3>
          <ResponsiveContainer width="100%" height={280}>
            <BarChart data={data}>
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
            <LineChart data={data}>
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
