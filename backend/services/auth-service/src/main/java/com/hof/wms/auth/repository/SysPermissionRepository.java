package com.hof.wms.auth.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hof.wms.auth.entity.SysPermission;
import com.hof.wms.auth.mapper.SysPermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SysPermissionRepository {

    private final SysPermissionMapper permissionMapper;

    public List<SysPermission> findAll() {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper();
        wrapper.orderByAsc(SysPermission::getSort);
        return permissionMapper.selectList(wrapper);
    }

    public List<SysPermission> findTree() {
        List<SysPermission> all = findAll();
        return buildTree(all, null);
    }

    private List<SysPermission> buildTree(List<SysPermission> all, Long parentId) {
        return all.stream()
                .filter(p -> (parentId == null && p.getParentId() == null) ||
                        (parentId != null && parentId.equals(p.getParentId())))
                .toList();
    }

    public SysPermission findById(Long id) {
        return permissionMapper.selectById(id);
    }

    public SysPermission save(SysPermission permission) {
        if (permission.getId() == null) {
            permissionMapper.insert(permission);
        } else {
            permissionMapper.updateById(permission);
        }
        return permission;
    }

    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }
}
