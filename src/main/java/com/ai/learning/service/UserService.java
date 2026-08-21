package com.ai.learning.service;

import com.ai.learning.entity.SysUser;

/**
 * 用户业务接口：定义“能干什么”
 */
public interface UserService {

    /**
     * 注册
     */
    void register(SysUser user);

    /**
     * 登录：校验通过返回用户信息（密码已清空）
     */
    SysUser login(String username,String password);
}
