import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Checkbox } from "@/components/ui/checkbox";
import { Warehouse, Lock, User } from "lucide-react";
import { toast } from "sonner";

export default function Login() {
  const nav = useNavigate();
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) { toast.error("请输入账号和密码"); return; }
    setLoading(true);
    setTimeout(() => {
      localStorage.setItem("wms_token", "mock-token");
      toast.success("登录成功");
      nav("/dashboard");
    }, 500);
  };

  return (
    <div className="min-h-screen grid lg:grid-cols-2">
      {/* 左侧品牌区 */}
      <div className="hidden lg:flex relative overflow-hidden flex-col justify-between p-12 text-primary-foreground" style={{ background: "var(--gradient-primary)" }}>
        <div className="flex items-center gap-3 relative z-10">
          <div className="h-11 w-11 rounded-lg bg-white/15 backdrop-blur flex items-center justify-center">
            <Warehouse className="h-6 w-6" />
          </div>
          <div>
            <div className="font-semibold text-lg">智仓 WMS</div>
            <div className="text-xs text-primary-foreground/70">Enterprise Warehouse Management</div>
          </div>
        </div>
        <div className="relative z-10 space-y-4">
          <h1 className="text-4xl font-semibold leading-tight">让仓储管理<br />更智能、更高效</h1>
          <p className="text-primary-foreground/80 text-sm max-w-md">入库、出库、库存、数据对接、智能分析一体化管理平台，助力企业降本增效。</p>
        </div>
        <div className="text-xs text-primary-foreground/60 relative z-10">© 2026 智仓 WMS · 企业级仓储管理平台</div>
        <div className="absolute -right-32 -top-32 h-96 w-96 rounded-full bg-white/10 blur-3xl" />
        <div className="absolute -left-20 -bottom-20 h-80 w-80 rounded-full bg-white/10 blur-3xl" />
      </div>

      {/* 右侧表单 */}
      <div className="flex items-center justify-center p-6 sm:p-12 bg-background">
        <form onSubmit={submit} className="w-full max-w-sm space-y-6">
          <div className="space-y-2">
            <h2 className="text-2xl font-semibold">欢迎回来</h2>
            <p className="text-sm text-muted-foreground">请使用您的账号登录系统</p>
          </div>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="u">账号</Label>
              <div className="relative">
                <User className="h-4 w-4 absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                <Input id="u" className="pl-9" value={username} onChange={e => setUsername(e.target.value)} placeholder="请输入账号" />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="p">密码</Label>
              <div className="relative">
                <Lock className="h-4 w-4 absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                <Input id="p" type="password" className="pl-9" value={password} onChange={e => setPassword(e.target.value)} placeholder="请输入密码" />
              </div>
            </div>
            <div className="flex items-center justify-between text-sm">
              <label className="flex items-center gap-2 text-muted-foreground"><Checkbox defaultChecked />记住我</label>
              <a href="#" className="text-primary hover:underline">忘记密码？</a>
            </div>
          </div>
          <Button type="submit" className="w-full" disabled={loading}>{loading ? "登录中..." : "登 录"}</Button>
          <p className="text-xs text-center text-muted-foreground">提示：演示环境随意输入密码即可登录</p>
        </form>
      </div>
    </div>
  );
}
