import { useState, useRef, useEffect } from "react";
import { PageHeader } from "@/components/PageHeader";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Sparkles, Send, Bot, User, TrendingUp } from "lucide-react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Area, AreaChart } from "recharts";

interface Message { role: "user" | "assistant"; content: string; }

const presets = ["分析最近30天的销售趋势", "预测下月各仓库的库存需求", "找出周转最慢的10个SKU", "对比各仓库的吞吐效率"];

const prediction = Array.from({ length: 12 }, (_, i) => ({
  月份: `第${i + 1}周`,
  实际: i < 8 ? 800 + ((i * 47) % 200) : 0,
  预测: 820 + ((i * 51) % 220),
}));

export default function AIAnalysis() {
  const [messages, setMessages] = useState<Message[]>([
    { role: "assistant", content: "您好！我是智仓 AI 分析助手 👋 \n\n我可以帮您：\n• 分析销售与库存趋势\n• 预测未来需求\n• 识别异常数据\n• 生成可视化报告\n\n试试点击下方的预设问题吧！" },
  ]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const endRef = useRef<HTMLDivElement>(null);
  useEffect(() => endRef.current?.scrollIntoView({ behavior: "smooth" }), [messages]);

  const send = (text?: string) => {
    const content = (text ?? input).trim();
    if (!content) return;
    setMessages(m => [...m, { role: "user", content }]);
    setInput("");
    setLoading(true);
    setTimeout(() => {
      setMessages(m => [...m, { role: "assistant", content: `根据您的问题"${content}"，分析结果如下：\n\n📊 近期数据呈现稳定上升趋势，平均增幅 12.5%。\n📈 预测下周需求将达到 1240 单，建议提前补货热销 SKU。\n⚠️ 识别到 3 个 SKU 出现异常波动，建议进一步排查。\n\n详细图表已在右侧展示。` }]);
      setLoading(false);
    }, 800);
  };

  return (
    <div className="page-container">
      <PageHeader title="AI 智能分析" subtitle="基于大模型的数据洞察与预测" breadcrumbs={[{ label: "报表分析" }, { label: "AI 智能分析" }]}
        actions={<div className="flex items-center gap-1.5 text-xs text-muted-foreground"><Sparkles className="h-3.5 w-3.5 text-primary" />Powered by Lovable AI</div>} />

      <div className="grid grid-cols-1 lg:grid-cols-5 gap-4 h-[calc(100vh-220px)]">
        {/* 对话区 */}
        <div className="panel lg:col-span-2 p-0 flex flex-col overflow-hidden">
          <div className="px-5 py-3 border-b border-border flex items-center gap-2">
            <Bot className="h-4 w-4 text-primary" /><span className="font-semibold text-sm">AI 对话</span>
          </div>
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.map((m, i) => (
              <div key={i} className={`flex gap-2.5 ${m.role === "user" ? "flex-row-reverse" : ""}`}>
                <div className={`h-8 w-8 rounded-md flex items-center justify-center shrink-0 ${m.role === "user" ? "bg-primary text-primary-foreground" : "bg-accent text-accent-foreground"}`}>
                  {m.role === "user" ? <User className="h-4 w-4" /> : <Bot className="h-4 w-4" />}
                </div>
                <div className={`max-w-[85%] px-3.5 py-2.5 rounded-lg text-sm whitespace-pre-wrap ${m.role === "user" ? "bg-primary text-primary-foreground" : "bg-muted text-foreground"}`}>
                  {m.content}
                </div>
              </div>
            ))}
            {loading && <div className="flex gap-2.5"><div className="h-8 w-8 rounded-md flex items-center justify-center bg-accent text-accent-foreground"><Bot className="h-4 w-4" /></div><div className="px-3.5 py-2.5 rounded-lg bg-muted text-sm text-muted-foreground">思考中...</div></div>}
            <div ref={endRef} />
          </div>
          <div className="border-t border-border p-3 space-y-2">
            <div className="flex flex-wrap gap-1.5">
              {presets.map(p => <button key={p} onClick={() => send(p)} className="text-xs px-2.5 py-1 rounded-md bg-accent text-accent-foreground hover:bg-primary hover:text-primary-foreground transition">{p}</button>)}
            </div>
            <div className="flex gap-2">
              <Input value={input} onChange={e => setInput(e.target.value)} onKeyDown={e => e.key === "Enter" && send()} placeholder="输入您的问题..." />
              <Button onClick={() => send()} disabled={loading} className="gap-1.5"><Send className="h-4 w-4" /></Button>
            </div>
          </div>
        </div>

        {/* 结果区 */}
        <div className="lg:col-span-3 space-y-4 overflow-y-auto">
          <div className="panel">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2"><TrendingUp className="h-4 w-4 text-primary" /><h3 className="font-semibold">库存需求预测（未来 4 周）</h3></div>
              <span className="text-xs text-muted-foreground">置信度 92%</span>
            </div>
            <ResponsiveContainer width="100%" height={260}>
              <AreaChart data={prediction}>
                <defs>
                  <linearGradient id="grad1" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="hsl(214 88% 38%)" stopOpacity={0.3} />
                    <stop offset="95%" stopColor="hsl(214 88% 38%)" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="hsl(var(--border))" />
                <XAxis dataKey="月份" stroke="hsl(var(--muted-foreground))" fontSize={12} />
                <YAxis stroke="hsl(var(--muted-foreground))" fontSize={12} />
                <Tooltip contentStyle={{ background: "hsl(var(--card))", border: "1px solid hsl(var(--border))", borderRadius: 8 }} />
                <Area type="monotone" dataKey="实际" stroke="hsl(214 88% 38%)" strokeWidth={2} fill="url(#grad1)" />
                <Line type="monotone" dataKey="预测" stroke="hsl(38 92% 50%)" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="stat-card"><p className="text-xs text-muted-foreground">预测准确率</p><p className="text-2xl font-semibold mt-1">92.4%</p><p className="text-xs text-success mt-1">↑ 较上月 +3.2%</p></div>
            <div className="stat-card"><p className="text-xs text-muted-foreground">异常 SKU</p><p className="text-2xl font-semibold mt-1">3</p><p className="text-xs text-warning mt-1">需要关注</p></div>
            <div className="stat-card"><p className="text-xs text-muted-foreground">建议补货</p><p className="text-2xl font-semibold mt-1">12</p><p className="text-xs text-info mt-1">SKU 低于安全线</p></div>
          </div>

          <div className="panel">
            <h3 className="font-semibold mb-3">AI 洞察建议</h3>
            <ul className="space-y-2.5 text-sm">
              <li className="flex gap-2.5"><span className="h-5 w-5 rounded-full bg-primary-soft text-primary text-xs flex items-center justify-center shrink-0 mt-0.5">1</span><span>3C 数码品类近 14 天周转率提升 18%，建议加大该品类备货量。</span></li>
              <li className="flex gap-2.5"><span className="h-5 w-5 rounded-full bg-primary-soft text-primary text-xs flex items-center justify-center shrink-0 mt-0.5">2</span><span>上海中心仓利用率已达 85%，建议启用成都分拣仓做分流。</span></li>
              <li className="flex gap-2.5"><span className="h-5 w-5 rounded-full bg-primary-soft text-primary text-xs flex items-center justify-center shrink-0 mt-0.5">3</span><span>SKU010008 等 5 款商品近 30 天零出库，建议清理长尾库存。</span></li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}
