import { NavLink, useLocation } from "react-router-dom";
import {
  Sidebar, SidebarContent, SidebarGroup, SidebarGroupContent, SidebarGroupLabel,
  SidebarMenu, SidebarMenuButton, SidebarMenuItem, SidebarHeader, SidebarFooter, useSidebar,
} from "@/components/ui/sidebar";
import {
  LayoutDashboard, PackagePlus, PackageMinus, Boxes, ClipboardCheck, AlertTriangle,
  RefreshCw, ListChecks, FileBarChart, Sparkles, Warehouse, Database,
  Users, ShieldCheck, KeyRound, Package, FolderTree,
} from "lucide-react";
import { cn } from "@/lib/utils";

interface NavItem { title: string; url: string; icon: React.ComponentType<{ className?: string }>; }
interface NavGroup { label: string; items: NavItem[]; }

const groups: NavGroup[] = [
  {
    label: "概览",
    items: [{ title: "仪表盘", url: "/dashboard", icon: LayoutDashboard }],
  },
  {
    label: "商品中心",
    items: [
      { title: "商品管理", url: "/sku/list", icon: Package },
      { title: "商品分类", url: "/sku/category", icon: FolderTree },
    ],
  },
  {
    label: "入库管理",
    items: [{ title: "入库单列表", url: "/inbound/list", icon: PackagePlus }],
  },
  {
    label: "出库销售",
    items: [{ title: "出库单列表", url: "/outbound/list", icon: PackageMinus }],
  },
  {
    label: "库存管理",
    items: [
      { title: "库存查询", url: "/inventory/list", icon: Boxes },
      { title: "库存盘点", url: "/inventory/check", icon: ClipboardCheck },
      { title: "库存预警", url: "/inventory/alert", icon: AlertTriangle },
    ],
  },
  {
    label: "数据对接",
    items: [
      { title: "同步任务", url: "/data-sync/list", icon: RefreshCw },
      { title: "同步日志", url: "/data-sync/log", icon: ListChecks },
    ],
  },
  {
    label: "报表分析",
    items: [
      { title: "报表中心", url: "/report/center", icon: FileBarChart },
      { title: "AI 智能分析", url: "/report/ai-analysis", icon: Sparkles },
    ],
  },
  {
    label: "系统管理",
    items: [
      { title: "用户管理", url: "/system/user", icon: Users },
      { title: "角色管理", url: "/system/role", icon: ShieldCheck },
      { title: "权限管理", url: "/system/permission", icon: KeyRound },
    ],
  },
];

export function AppSidebar() {
  const { state } = useSidebar();
  const collapsed = state === "collapsed";
  const { pathname } = useLocation();

  return (
    <Sidebar collapsible="icon" className="border-r-0">
      <SidebarHeader className="border-b border-sidebar-border">
        <div className="flex items-center gap-2.5 px-2 py-2">
          <div className="h-9 w-9 rounded-md bg-sidebar-primary flex items-center justify-center shrink-0">
            <Warehouse className="h-5 w-5 text-sidebar-primary-foreground" />
          </div>
          {!collapsed && (
            <div className="flex flex-col leading-tight">
              <span className="text-sidebar-primary-foreground font-semibold text-sm">智仓 WMS</span>
              <span className="text-xs text-sidebar-foreground/60">Enterprise Edition</span>
            </div>
          )}
        </div>
      </SidebarHeader>

      <SidebarContent className="px-1.5">
        {groups.map(g => (
          <SidebarGroup key={g.label}>
            {!collapsed && <SidebarGroupLabel className="text-sidebar-foreground/50 text-[11px] uppercase tracking-wider font-medium px-2">{g.label}</SidebarGroupLabel>}
            <SidebarGroupContent>
              <SidebarMenu>
                {g.items.map(item => {
                  const active = pathname === item.url || pathname.startsWith(item.url.replace("/list", "/"));
                  return (
                    <SidebarMenuItem key={item.url}>
                      <SidebarMenuButton asChild isActive={active}>
                        <NavLink
                          to={item.url}
                          className={cn(
                            "flex items-center gap-2.5 rounded-md transition-colors",
                            "text-sidebar-foreground hover:bg-sidebar-accent hover:text-sidebar-accent-foreground",
                            active && "bg-sidebar-primary text-sidebar-primary-foreground hover:bg-sidebar-primary hover:text-sidebar-primary-foreground"
                          )}
                        >
                          <item.icon className="h-4 w-4 shrink-0" />
                          {!collapsed && <span className="text-sm">{item.title}</span>}
                        </NavLink>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  );
                })}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        ))}
      </SidebarContent>

      <SidebarFooter className="border-t border-sidebar-border">
        {!collapsed && (
          <div className="px-2 py-1.5 flex items-center gap-2 text-xs text-sidebar-foreground/60">
            <Database className="h-3.5 w-3.5" />
            <span>v1.0.0 · Build 2026.04</span>
          </div>
        )}
      </SidebarFooter>
    </Sidebar>
  );
}
