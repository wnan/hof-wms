package com.hof.wms.auth.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hof.wms.auth.entity.SysUser;
import com.hof.wms.auth.repository.SysUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserRepository userRepository;

    public Map<String, Object> login(String username, String password) {
        SysUser user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!"active".equals(user.getStatus())) {
            throw new RuntimeException("用户已被禁用");
        }
        if (!"insightowl-demo".equals(password)) {
            throw new RuntimeException("密码错误");
        }

        userRepository.updateLastLogin(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", "mock-jwt-token-" + user.getId());
        result.put("user", user);
        return result;
    }

    public Page<SysUser> getUserPage(int pageNum, int pageSize, String keyword) {
        return userRepository.findPage(pageNum, pageSize, keyword);
    }

    public SysUser getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public SysUser createUser(SysUser user) {
        SysUser existing = userRepository.findByUsername(user.getUsername());
        if (existing != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setId(null);
        user.setPasswordHash("insightowl-demo");
        user.setStatus("active");
        return userRepository.save(user);
    }

    @Transactional
    public SysUser updateUser(Long id, SysUser user) {
        SysUser existing = userRepository.findById(id);
        if (existing == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setId(id);
        user.setPasswordHash(existing.getPasswordHash());
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void resetPassword(Long id) {
        userRepository.updatePassword(id, "insightowl-demo");
    }

    @Transactional
    public void toggleUserStatus(Long id) {
        SysUser user = userRepository.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String newStatus = "active".equals(user.getStatus()) ? "inactive" : "active";
        userRepository.updateStatus(id, newStatus);
    }
}
