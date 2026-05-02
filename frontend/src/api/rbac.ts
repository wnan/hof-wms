import { request, type PageParams, type PageResult } from "./request";
import type { SysUser, SysRole, SysPermission } from "@/types/rbac";

export const userApi = {
  list: (params: PageParams) => request.get<PageResult<SysUser>>("/system/user/list", params),
  save: (data: Partial<SysUser>) => request.post<SysUser>("/system/user/save", data),
  remove: (id: string) => request.del<void>(`/system/user/${id}`),
  resetPwd: (id: string) => request.post<void>(`/system/user/${id}/reset-password`),
  setStatus: (id: string, status: SysUser["status"]) => request.post<void>(`/system/user/${id}/status`, { status }),
};

export const roleApi = {
  list: (params: PageParams) => request.get<PageResult<SysRole>>("/system/role/list", params),
  save: (data: Partial<SysRole>) => request.post<SysRole>("/system/role/save", data),
  remove: (id: string) => request.del<void>(`/system/role/${id}`),
  assignPerms: (id: string, permissionIds: string[]) => request.post<void>(`/system/role/${id}/permissions`, { permissionIds }),
};

export const permissionApi = {
  tree: () => request.get<SysPermission[]>("/system/permission/tree"),
  save: (data: Partial<SysPermission>) => request.post<SysPermission>("/system/permission/save", data),
  remove: (id: string) => request.del<void>(`/system/permission/${id}`),
};
