package com.hof.wms.auth.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hof.wms.auth.entity.SysRole;
import com.hof.wms.auth.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SysRoleRepository {

    private final SysRoleMapper roleMapper;

    public List<SysRole> findAll() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper();
        wrapper.orderByAsc(SysRole::getCreatedAt);
        return roleMapper.selectList(wrapper);
    }

    public SysRole findById(Long id) {
        return roleMapper.selectById(id);
    }

    public SysRole save(SysRole role) {
        if (role.getId() == null) {
            role.setCreatedAt(LocalDateTime.now());
            role.setStatus("active");
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        return role;
    }

    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }
}
