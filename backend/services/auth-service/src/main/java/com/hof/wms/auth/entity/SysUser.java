package com.hof.wms.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value = "sys_user", schema = "auth")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField("real_name")
    private String realName;

    private String email;

    private String phone;

    private String department;

    private String status;

    @TableField("password_hash")
    private String passwordHash;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
