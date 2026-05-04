package com.hof.wms.auth.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.auth.entity.SysPermission;
import com.hof.wms.auth.entity.SysRole;
import com.hof.wms.auth.entity.SysUser;
import com.hof.wms.auth.service.AuthService;
import com.hof.wms.auth.service.SystemService;
import com.hof.wms.common.result.ApiResult;
import com.hof.wms.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SystemService systemService;

    @PostMapping("/auth/login")
    public ApiResult<Map<String, Object>> login(@RequestBody Map<String, String> command) {
        String username = command.getOrDefault("username", "");
        String password = command.getOrDefault("password", "");
        Map<String, Object> result = authService.login(username, password);
        return ApiResult.success("登录成功", result);
    }

    @GetMapping("/system/user/list")
    public ApiResult<PageResult<Map<String, Object>>> userList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        Page<SysUser> page = authService.getUserPage(pageNum, pageSize, keyword);
        List<Map<String, Object>> rows = page.getRecords().stream()
                .map(this::toUserMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, page.getTotal(), (long) pageNum, (long) pageSize));
    }

    @PostMapping("/system/user/save")
    public ApiResult<Map<String, Object>> userSave(@RequestBody Map<String, Object> command) {
        SysUser user = toUser(command);
        SysUser saved;
        if (user.getId() != null) {
            saved = authService.updateUser(user.getId(), user);
            return ApiResult.success("更新成功", toUserMap(saved));
        } else {
            saved = authService.createUser(user);
            return ApiResult.success("创建成功", toUserMap(saved));
        }
    }

    @DeleteMapping("/system/user/{id}")
    public ApiResult<Void> userDelete(@PathVariable Long id) {
        authService.deleteUser(id);
        return ApiResult.success("删除成功", null);
    }

    @PostMapping("/system/user/{id}/reset-password")
    public ApiResult<Void> resetPassword(@PathVariable Long id) {
        authService.resetPassword(id);
        return ApiResult.success("密码已重置", null);
    }

    @PostMapping("/system/user/{id}/status")
    public ApiResult<Void> userStatus(@PathVariable Long id) {
        authService.toggleUserStatus(id);
        return ApiResult.success("状态已更新", null);
    }

    @GetMapping("/system/role/list")
    public ApiResult<PageResult<Map<String, Object>>> roleList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<SysRole> roles = systemService.getRoleList();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, roles.size());
        List<Map<String, Object>> rows = roles.subList(start, end).stream()
                .map(this::toRoleMap)
                .collect(Collectors.toList());
        return ApiResult.success(new PageResult<>(rows, (long) roles.size(), (long) pageNum, (long) pageSize));
    }

    @PostMapping("/system/role/save")
    public ApiResult<Map<String, Object>> roleSave(@RequestBody Map<String, Object> command) {
        SysRole role = toRole(command);
        SysRole saved;
        if (role.getId() != null) {
            saved = systemService.updateRole(role.getId(), role);
            return ApiResult.success("更新成功", toRoleMap(saved));
        } else {
            saved = systemService.createRole(role);
            return ApiResult.success("创建成功", toRoleMap(saved));
        }
    }

    @DeleteMapping("/system/role/{id}")
    public ApiResult<Void> roleDelete(@PathVariable Long id) {
        systemService.deleteRole(id);
        return ApiResult.success("删除成功", null);
    }

    @PostMapping("/system/role/{id}/permissions")
    public ApiResult<Void> rolePermissions(@PathVariable Long id, @RequestBody Map<String, Object> command) {
        return ApiResult.success("权限已更新", null);
    }

    @GetMapping("/system/permission/tree")
    public ApiResult<List<Map<String, Object>>> permissionTree() {
        List<SysPermission> tree = systemService.getPermissionTree();
        return ApiResult.success(tree.stream().map(this::toPermissionMap).collect(Collectors.toList()));
    }

    @PostMapping("/system/permission/save")
    public ApiResult<Map<String, Object>> permissionSave(@RequestBody Map<String, Object> command) {
        SysPermission permission = toPermission(command);
        SysPermission saved = systemService.createPermission(permission);
        return ApiResult.success("创建成功", toPermissionMap(saved));
    }

    @DeleteMapping("/system/permission/{id}")
    public ApiResult<Void> permissionDelete(@PathVariable Long id) {
        systemService.deletePermission(id);
        return ApiResult.success("删除成功", null);
    }

    private Map<String, Object> toUserMap(SysUser user) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("realName", user.getRealName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("department", user.getDepartment());
        map.put("status", user.getStatus());
        map.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString().replace("T", " ") : null);
        return map;
    }

    private SysUser toUser(Map<String, Object> map) {
        SysUser user = new SysUser();
        if (map.get("id") != null) {
            user.setId(((Number) map.get("id")).longValue());
        }
        user.setUsername((String) map.get("username"));
        user.setRealName((String) map.get("realName"));
        user.setEmail((String) map.get("email"));
        user.setPhone((String) map.get("phone"));
        user.setDepartment((String) map.get("department"));
        user.setStatus((String) map.get("status"));
        return user;
    }

    private Map<String, Object> toRoleMap(SysRole role) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", role.getId());
        map.put("code", role.getCode());
        map.put("name", role.getName());
        map.put("description", role.getDescription());
        map.put("status", role.getStatus());
        map.put("createdAt", role.getCreatedAt() != null ? role.getCreatedAt().toString().replace("T", " ") : null);
        return map;
    }

    private SysRole toRole(Map<String, Object> map) {
        SysRole role = new SysRole();
        if (map.get("id") != null) {
            role.setId(((Number) map.get("id")).longValue());
        }
        role.setCode((String) map.get("code"));
        role.setName((String) map.get("name"));
        role.setDescription((String) map.get("description"));
        role.setStatus((String) map.get("status"));
        return role;
    }

    private Map<String, Object> toPermissionMap(SysPermission permission) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", permission.getId());
        map.put("name", permission.getName());
        map.put("code", permission.getCode());
        map.put("type", permission.getType());
        map.put("path", permission.getPath());
        map.put("parentId", permission.getParentId());
        map.put("sort", permission.getSort());
        return map;
    }

    private SysPermission toPermission(Map<String, Object> map) {
        SysPermission permission = new SysPermission();
        if (map.get("id") != null) {
            permission.setId(((Number) map.get("id")).longValue());
        }
        if (map.get("parentId") != null) {
            permission.setParentId(((Number) map.get("parentId")).longValue());
        }
        permission.setName((String) map.get("name"));
        permission.setCode((String) map.get("code"));
        permission.setType((String) map.get("type"));
        permission.setPath((String) map.get("path"));
        if (map.get("sort") != null) {
            permission.setSort(((Number) map.get("sort")).intValue());
        }
        return permission;
    }
}
