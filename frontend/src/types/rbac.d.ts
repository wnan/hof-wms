export type UserStatus = "active" | "disabled";

export interface SysUser {
  id: string;
  username: string;
  realName: string;
  email: string;
  phone: string;
  department: string;
  roleIds: string[];
  status: UserStatus;
  lastLoginAt?: string;
  createdAt: string;
}

export interface SysRole {
  id: string;
  code: string;
  name: string;
  description: string;
  permissionIds: string[];
  userCount: number;
  status: UserStatus;
  createdAt: string;
}

// 权限树节点：菜单 / 按钮 / 接口
export type PermissionType = "menu" | "button" | "api";

export interface SysPermission {
  id: string;
  parentId: string | null;
  name: string;
  code: string;        // 例如 inbound:list / inbound:create
  type: PermissionType;
  path?: string;       // 菜单路径
  icon?: string;
  sort: number;
  children?: SysPermission[];
}
