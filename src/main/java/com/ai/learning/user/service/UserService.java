package com.ai.learning.user.service;

import com.ai.learning.user.dto.LoginDTO;
import com.ai.learning.user.dto.ProfileDTO;
import com.ai.learning.user.dto.RegisterDTO;
import com.ai.learning.user.dto.ResetPasswordDTO;
import com.ai.learning.user.entity.SysUser;
import com.ai.learning.user.vo.UserVO;

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

    /**
     *更新个人信息，返回更新后的用户
     */
    UserVO updateProfile(Long userId, ProfileDTO dto);

    SysUser findById(Long id);
}
