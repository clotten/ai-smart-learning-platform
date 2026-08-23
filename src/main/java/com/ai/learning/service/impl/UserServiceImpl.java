package com.ai.learning.service.impl;

import com.ai.learning.common.BusinessException;
import com.ai.learning.entity.SysUser;
import com.ai.learning.mapper.SysUserMapper;
import com.ai.learning.service.RateLimitService;
import com.ai.learning.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户业务实现：真正干活的类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{


    private final SysUserMapper userMapper;
    private final RateLimitService rateLimitService;

    //Bcrypto加密器（spring-security-crypto提供）
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void register(SysUser user){
        //1.检查用户名是否已存在
        Long count = userMapper.selectCount(
                new QueryWrapper<SysUser>().eq("username",user.getUsername()));
        if(count > 0){
            throw new BusinessException("用户名已存在");
        }
        //2.密码加密后再存库（绝不存明文！）
        user.setPassword(encoder.encode(user.getPassword()));
        //3.默认角色：学生（2）
        if(user.getRole() == null){
            user.setRole(2);
        }
        //4.插入数据库
        userMapper.insert(user);
        log.info("用户注册成功：{}",user.getUsername());
    }

    @Override
    public SysUser login(String username,String password){
        //1.按用户名查用户
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("username",username));
        if(user == null){
            throw new BusinessException("用户名不存在");
        }
        //  黑名单检查：按查到的用户 id（被限制的直接拒绝登录）
        if (rateLimitService.isBlocked(user.getId())) {
            throw new BusinessException("账号已被临时限制，请明天再试");
        }
        //2.比对密码（matches = 把明文再加密一次和库里密文比）
        if(!encoder.matches(password,user.getPassword())){
            throw new BusinessException("密码错误");
        }
        return user;
    }
}
