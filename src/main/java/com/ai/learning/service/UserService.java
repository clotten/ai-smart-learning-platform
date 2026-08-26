package com.ai.learning.service;

import com.ai.learning.dto.LoginDTO;
import com.ai.learning.dto.RegisterDTO;
import com.ai.learning.dto.ResetPasswordDTO;
import com.ai.learning.entity.SysUser;

/**
 * 用户业务接口：定义“能干什么”
 */
public interface UserService {

    /**
     * 邮箱注册
     */
    void register(RegisterDTO dto);

    /**
     * 邮箱+密码登录
     */
    SysUser login(LoginDTO dto);

    /**
     * 邮箱验证码登录（未注册自动注册，无密码）
     */
    SysUser loginByEmail(String email);

    /**
     * Github登录
     */
    SysUser loginByGithub(Long githubId, String githubLogin);
    /**
     * 设置/重置密码（验证码验证身份）
     */
    void resetPassword(ResetPasswordDTO dto);

    SysUser findById(Long id);
}
