package com.hof.wms.auth.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.auth.entity.SysUser;
import com.hof.wms.auth.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class SysUserRepository {

    private final SysUserMapper userMapper;

    public Page<SysUser> findPage(int pageNum, int pageSize, String keyword) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword)
                    .or().like(SysUser::getDepartment, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        return userMapper.selectPage(page, wrapper);
    }

    public SysUser findById(Long id) {
        return userMapper.selectById(id);
    }

    public SysUser findByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(wrapper);
    }

    public SysUser save(SysUser user) {
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.insert(user);
        } else {
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
        return user;
    }

    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    public void updatePassword(Long id, String passwordHash) {
        SysUser user = findById(id);
        if (user != null) {
            user.setPasswordHash(passwordHash);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    public void updateStatus(Long id, String status) {
        SysUser user = findById(id);
        if (user != null) {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }

    public void updateLastLogin(Long id) {
        SysUser user = findById(id);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            userMapper.updateById(user);
        }
    }
}
