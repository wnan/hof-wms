package com.hof.wms.auth.service;

import com.hof.wms.common.result.PageResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthMockService {

    public Map<String, Object> login(String username) {
        return Map.of(
                "token", "jwt-demo-token",
                "user", Map.of(
                        "id", "u1000",
                        "username", username,
                        "realName", "管理员",
                        "email", "admin@wms.com",
                        "phone", "13800000000",
                        "department", "技术部",
                        "roleIds", List.of("r1"),
                        "status", "active",
                        "createdAt", "2026-01-01 10:00:00"
                )
        );
    }

    public PageResult<Map<String, Object>> userPage() {
        List<Map<String, Object>> rows = List.of(
                Map.of("id", "u1000", "username", "admin", "realName", "管理员1", "email", "admin@wms.com", "phone", "13800000000", "department", "技术部", "roleIds", List.of("r1"), "status", "active", "lastLoginAt", "2026-05-03 10:00:00", "createdAt", "2026-01-01 10:00:00"),
                Map.of("id", "u1001", "username", "operator001", "realName", "仓库操作员", "email", "op@wms.com", "phone", "13800000001", "department", "供应链部", "roleIds", List.of("r2"), "status", "active", "lastLoginAt", "2026-05-02 09:30:00", "createdAt", "2026-01-05 10:00:00")
        );
        return new PageResult<>(rows, (long) rows.size(), 1L, 10L);
    }

    public PageResult<Map<String, Object>> rolePage() {
        List<Map<String, Object>> rows = List.of(
                Map.of("id", "r1", "code", "super_admin", "name", "超级管理员", "description", "拥有系统全部权限", "permissionIds", List.of("*"), "userCount", 1, "status", "active", "createdAt", "2026-01-01 09:00:00"),
                Map.of("id", "r2", "code", "warehouse_admin", "name", "仓库管理员", "description", "管理仓储业务", "permissionIds", List.of("p-in", "p-out", "p-inv"), "userCount", 5, "status", "active", "createdAt", "2026-01-05 10:00:00")
        );
        return new PageResult<>(rows, (long) rows.size(), 1L, 10L);
    }

    public List<Map<String, Object>> permissionTree() {
        return List.of(
                Map.of("id", "p-dash", "parentId", null, "name", "仪表盘", "code", "dashboard", "type", "menu", "path", "/dashboard", "sort", 1),
                Map.of("id", "p-in", "parentId", null, "name", "入库管理", "code", "inbound", "type", "menu", "path", "/inbound/list", "sort", 2)
        );
    }
}
