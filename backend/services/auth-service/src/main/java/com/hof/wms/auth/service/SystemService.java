package com.hof.wms.auth.service;

import com.hof.wms.auth.entity.SysPermission;
import com.hof.wms.auth.entity.SysRole;
import com.hof.wms.auth.repository.SysPermissionRepository;
import com.hof.wms.auth.repository.SysRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final SysRoleRepository roleRepository;
    private final SysPermissionRepository permissionRepository;

    public List<SysRole> getRoleList() {
        return roleRepository.findAll();
    }

    public SysRole getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Transactional
    public SysRole createRole(SysRole role) {
        role.setId(null);
        return roleRepository.save(role);
    }

    @Transactional
    public SysRole updateRole(Long id, SysRole role) {
        SysRole existing = roleRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("角色不存在");
        }
        role.setId(id);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public List<SysPermission> getPermissionTree() {
        return permissionRepository.findTree();
    }

    public SysPermission getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Transactional
    public SysPermission createPermission(SysPermission permission) {
        permission.setId(null);
        return permissionRepository.save(permission);
    }

    @Transactional
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }
}
