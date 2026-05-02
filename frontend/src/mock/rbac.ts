import type { SysUser, SysRole, SysPermission } from "@/types/rbac";

export const mockPermissions: SysPermission[] = [
  {
    id: "p-dash", parentId: null, name: "仪表盘", code: "dashboard", type: "menu", path: "/dashboard", sort: 1,
    children: [{ id: "p-dash-view", parentId: "p-dash", name: "查看", code: "dashboard:view", type: "button", sort: 1 }],
  },
  {
    id: "p-in", parentId: null, name: "入库管理", code: "inbound", type: "menu", path: "/inbound", sort: 2,
    children: [
      { id: "p-in-list", parentId: "p-in", name: "入库单列表", code: "inbound:list", type: "menu", path: "/inbound/list", sort: 1, children: [
        { id: "p-in-create", parentId: "p-in-list", name: "新建", code: "inbound:create", type: "button", sort: 1 },
        { id: "p-in-edit", parentId: "p-in-list", name: "编辑", code: "inbound:edit", type: "button", sort: 2 },
        { id: "p-in-delete", parentId: "p-in-list", name: "删除", code: "inbound:delete", type: "button", sort: 3 },
        { id: "p-in-approve", parentId: "p-in-list", name: "审核", code: "inbound:approve", type: "button", sort: 4 },
        { id: "p-in-export", parentId: "p-in-list", name: "导出", code: "inbound:export", type: "button", sort: 5 },
      ]},
    ],
  },
  {
    id: "p-out", parentId: null, name: "出库销售", code: "outbound", type: "menu", path: "/outbound", sort: 3,
    children: [
      { id: "p-out-list", parentId: "p-out", name: "出库单列表", code: "outbound:list", type: "menu", path: "/outbound/list", sort: 1, children: [
        { id: "p-out-create", parentId: "p-out-list", name: "新建", code: "outbound:create", type: "button", sort: 1 },
        { id: "p-out-ship", parentId: "p-out-list", name: "出库确认", code: "outbound:ship", type: "button", sort: 2 },
      ]},
    ],
  },
  {
    id: "p-inv", parentId: null, name: "库存管理", code: "inventory", type: "menu", path: "/inventory", sort: 4,
    children: [
      { id: "p-inv-list", parentId: "p-inv", name: "库存查询", code: "inventory:list", type: "menu", path: "/inventory/list", sort: 1 },
      { id: "p-inv-check", parentId: "p-inv", name: "库存盘点", code: "inventory:check", type: "menu", path: "/inventory/check", sort: 2 },
      { id: "p-inv-alert", parentId: "p-inv", name: "库存预警", code: "inventory:alert", type: "menu", path: "/inventory/alert", sort: 3 },
    ],
  },
  {
    id: "p-sync", parentId: null, name: "数据对接", code: "data-sync", type: "menu", path: "/data-sync", sort: 5,
    children: [
      { id: "p-sync-list", parentId: "p-sync", name: "同步任务", code: "data-sync:list", type: "menu", path: "/data-sync/list", sort: 1 },
      { id: "p-sync-log", parentId: "p-sync", name: "同步日志", code: "data-sync:log", type: "menu", path: "/data-sync/log", sort: 2 },
    ],
  },
  {
    id: "p-rep", parentId: null, name: "报表分析", code: "report", type: "menu", path: "/report", sort: 6,
    children: [
      { id: "p-rep-center", parentId: "p-rep", name: "报表中心", code: "report:center", type: "menu", path: "/report/center", sort: 1 },
      { id: "p-rep-ai", parentId: "p-rep", name: "AI 分析", code: "report:ai", type: "menu", path: "/report/ai-analysis", sort: 2 },
    ],
  },
  {
    id: "p-sys", parentId: null, name: "系统管理", code: "system", type: "menu", path: "/system", sort: 7,
    children: [
      { id: "p-sys-user", parentId: "p-sys", name: "用户管理", code: "system:user", type: "menu", path: "/system/user", sort: 1, children: [
        { id: "p-sys-user-add", parentId: "p-sys-user", name: "新增用户", code: "system:user:add", type: "button", sort: 1 },
        { id: "p-sys-user-edit", parentId: "p-sys-user", name: "编辑用户", code: "system:user:edit", type: "button", sort: 2 },
        { id: "p-sys-user-delete", parentId: "p-sys-user", name: "删除用户", code: "system:user:delete", type: "button", sort: 3 },
        { id: "p-sys-user-reset", parentId: "p-sys-user", name: "重置密码", code: "system:user:reset", type: "button", sort: 4 },
      ]},
      { id: "p-sys-role", parentId: "p-sys", name: "角色管理", code: "system:role", type: "menu", path: "/system/role", sort: 2 },
      { id: "p-sys-perm", parentId: "p-sys", name: "权限管理", code: "system:permission", type: "menu", path: "/system/permission", sort: 3 },
    ],
  },
];

export const mockRoles: SysRole[] = [
  { id: "r1", code: "super_admin", name: "超级管理员", description: "拥有系统全部权限", permissionIds: ["*"], userCount: 1, status: "active", createdAt: "2026-01-01 09:00:00" },
  { id: "r2", code: "warehouse_admin", name: "仓库管理员", description: "管理入库、出库、库存", permissionIds: ["p-dash","p-in","p-out","p-inv"], userCount: 5, status: "active", createdAt: "2026-01-05 10:00:00" },
  { id: "r3", code: "operator", name: "仓库操作员", description: "执行日常出入库操作", permissionIds: ["p-dash","p-in-list","p-out-list","p-inv-list"], userCount: 12, status: "active", createdAt: "2026-01-10 14:00:00" },
  { id: "r4", code: "auditor", name: "审计员", description: "查看报表和审计日志", permissionIds: ["p-dash","p-rep"], userCount: 3, status: "active", createdAt: "2026-02-01 11:00:00" },
  { id: "r5", code: "viewer", name: "只读用户", description: "仅查看库存与报表", permissionIds: ["p-dash","p-inv-list","p-rep-center"], userCount: 8, status: "disabled", createdAt: "2026-02-15 16:00:00" },
];

const depts = ["技术部", "运营中心", "供应链部", "财务部", "客户服务部"];
export const mockUsers: SysUser[] = Array.from({ length: 18 }, (_, i) => {
  const realNames = ["张伟","王芳","李娜","刘强","陈静","杨洋","赵磊","黄敏","周杰","吴桐","郑爽","孙琪","马超","朱琳","胡斌","郭涛","何蕾","高山"];
  return {
    id: `u${1000 + i}`,
    username: i === 0 ? "admin" : `user${String(i).padStart(3, "0")}`,
    realName: realNames[i],
    email: `${i === 0 ? "admin" : `user${i}`}@wms.com`,
    phone: `138****${String(1000 + i).slice(-4)}`,
    department: depts[i % depts.length],
    roleIds: i === 0 ? ["r1"] : [mockRoles[(i % 4) + 1].id],
    status: i % 7 === 6 ? "disabled" : "active",
    lastLoginAt: `2026-04-${String(28 - (i % 20)).padStart(2, "0")} 09:${String((i * 7) % 60).padStart(2, "0")}:00`,
    createdAt: `2026-0${(i % 4) + 1}-${String((i % 28) + 1).padStart(2, "0")} 10:00:00`,
  };
});
