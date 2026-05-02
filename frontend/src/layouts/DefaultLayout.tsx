import { Outlet, useLocation, useNavigate } from "react-router-dom";
import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/AppSidebar";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Bell, HelpCircle, LogOut, Settings, User } from "lucide-react";

const titleMap: Record<string, string> = {
  "/dashboard": "仪表盘",
  "/sku/list": "商品管理",
  "/sku/detail": "商品详情",
  "/sku/category": "商品分类",
  "/inbound/list": "入库单列表",
  "/inbound/detail": "入库单详情",
  "/outbound/list": "出库单列表",
  "/outbound/detail": "出库单详情",
  "/inventory/list": "库存查询",
  "/inventory/check": "库存盘点",
  "/inventory/alert": "库存预警",
  "/data-sync/list": "同步任务",
  "/data-sync/config": "同步任务配置",
  "/data-sync/log": "同步日志",
  "/report/center": "报表中心",
  "/report/detail": "报表详情",
  "/report/ai-analysis": "AI 智能分析",
  "/system/user": "用户管理",
  "/system/role": "角色管理",
  "/system/permission": "权限管理",
};

function currentTitle(path: string) {
  const hit = Object.keys(titleMap).find(k => path.startsWith(k));
  return hit ? titleMap[hit] : "智仓 WMS";
}

export default function DefaultLayout() {
  const { pathname } = useLocation();
  const navigate = useNavigate();
  return (
    <SidebarProvider defaultOpen>
      <div className="min-h-screen flex w-full bg-background">
        <AppSidebar />
        <div className="flex-1 flex flex-col min-w-0">
          <header className="h-14 bg-card border-b border-border flex items-center justify-between px-4 shrink-0 sticky top-0 z-10">
            <div className="flex items-center gap-3">
              <SidebarTrigger />
              <div className="h-5 w-px bg-border" />
              <h2 className="text-sm font-medium text-foreground">{currentTitle(pathname)}</h2>
            </div>
            <div className="flex items-center gap-1">
              <Button variant="ghost" size="icon" className="h-9 w-9"><HelpCircle className="h-4 w-4" /></Button>
              <Button variant="ghost" size="icon" className="h-9 w-9 relative">
                <Bell className="h-4 w-4" />
                <span className="absolute top-1.5 right-1.5 h-2 w-2 rounded-full bg-destructive" />
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="h-9 gap-2 px-2">
                    <Avatar className="h-7 w-7"><AvatarFallback className="bg-primary text-primary-foreground text-xs">管</AvatarFallback></Avatar>
                    <span className="text-sm">管理员</span>
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-48">
                  <DropdownMenuLabel>admin@wms.local</DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem><User className="h-4 w-4 mr-2" />个人中心</DropdownMenuItem>
                  <DropdownMenuItem><Settings className="h-4 w-4 mr-2" />系统设置</DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => navigate("/login")}><LogOut className="h-4 w-4 mr-2" />退出登录</DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </header>
          <main className="flex-1 overflow-auto">
            <Outlet />
          </main>
          <footer className="h-9 border-t border-border bg-card flex items-center justify-center text-xs text-muted-foreground">
            © 2026 智仓 WMS · 企业级仓储管理平台
          </footer>
        </div>
      </div>
    </SidebarProvider>
  );
}
