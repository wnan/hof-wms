import { request } from "./request";
import type { SysUser } from "@/types/rbac";

export interface LoginResponse {
  token: string;
  user: SysUser;
}

export const authApi = {
  login: (username: string, password: string) =>
    request.post<LoginResponse>("/auth/login", { username, password }),
};
