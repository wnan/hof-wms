// 统一请求封装：响应格式遵循文档约定 ApiResponse<T> / PageResult<T>
// 替换 BASE_URL 即可对接真实后端
import { toast } from "sonner";

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

export interface PageParams {
  current?: number;
  size?: number;
  [key: string]: unknown;
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";
const TIMEOUT = 30_000;

function getToken(): string | null {
  return localStorage.getItem("wms_token");
}

function buildUrl(path: string, params?: Record<string, unknown>): string {
  const url = new URL(path.startsWith("http") ? path : `${BASE_URL}${path}`, window.location.origin);
  if (params) {
    Object.entries(params).forEach(([k, v]) => {
      if (v !== undefined && v !== null && v !== "") url.searchParams.append(k, String(v));
    });
  }
  return url.toString();
}

async function coreFetch<T>(
  method: string,
  path: string,
  options: { params?: Record<string, unknown>; body?: unknown; retry?: number } = {}
): Promise<T> {
  const { params, body, retry = 2 } = options;
  const ctrl = new AbortController();
  const timer = setTimeout(() => ctrl.abort(), TIMEOUT);

  const headers: Record<string, string> = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) headers.Authorization = `Bearer ${token}`;

  try {
    const res = await fetch(buildUrl(path, params), {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
      signal: ctrl.signal,
    });
    clearTimeout(timer);

    if (res.status === 401) {
      toast.error("登录已过期，请重新登录");
      window.location.href = "/login";
      throw new Error("Unauthorized");
    }
    if (res.status === 403) {
      toast.error("没有权限访问该资源");
      throw new Error("Forbidden");
    }
    if (res.status >= 500) throw new Error(`Server ${res.status}`);

    const json = (await res.json()) as ApiResponse<T>;
    if (json.code !== 0) {
      toast.error(json.message || "请求失败");
      throw new Error(json.message);
    }
    return json.data;
  } catch (err) {
    clearTimeout(timer);
    if (retry > 0 && err instanceof TypeError) {
      // 网络错误重试
      return coreFetch<T>(method, path, { ...options, retry: retry - 1 });
    }
    throw err;
  }
}

export const request = {
  get: <T>(path: string, params?: Record<string, unknown>) => coreFetch<T>("GET", path, { params }),
  post: <T>(path: string, body?: unknown) => coreFetch<T>("POST", path, { body }),
  put: <T>(path: string, body?: unknown) => coreFetch<T>("PUT", path, { body }),
  del: <T>(path: string, params?: Record<string, unknown>) => coreFetch<T>("DELETE", path, { params }),
};
