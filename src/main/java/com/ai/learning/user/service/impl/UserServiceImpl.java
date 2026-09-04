package com.ai.learning.user.service.impl;

import com.ai.learning.common.BusinessException;
import com.ai.learning.user.dto.LoginDTO;
import com.ai.learning.user.dto.ProfileDTO;
import com.ai.learning.user.dto.RegisterDTO;
import com.ai.learning.user.dto.ResetPasswordDTO;
import com.ai.learning.user.entity.SysUser;
import com.ai.learning.user.mapper.SysUserMapper;
import com.ai.learning.common.service.RateLimitService;
import com.ai.learning.user.service.UserService;

import com.ai.learning.user.service.VerifyCodeService;
import com.ai.learning.user.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

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
    private final VerifyCodeService verifyCodeService;

    @Override
    public void register(RegisterDTO dto){
        //1.先验证邮箱归属（用后即焚的验证码）
        verifyCodeService.verify(dto.getEmail(), dto.getCode());
        //2.邮箱查重
        Long count = userMapper.selectCount(
                new QueryWrapper<SysUser>().eq("email",dto.getEmail()));
        if(count > 0){
            throw new BusinessException("该邮箱已注册");
        }
        //3.建号
        SysUser user = new SysUser();
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setUsername(generateUsername(dto.getUsername(), dto.getEmail()));
        user.setRole(2);
        userMapper.insert(user);
        log.info("用户注册成功：{}",user.getUsername());
    }

    @Override
    public SysUser login(LoginDTO dto){
        //1.按邮箱查
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("email",dto.getEmail()));
        if(user == null){
            throw new BusinessException("该邮箱未注册");
        }
        //2. 密码比对（验证码注册的用户没密码 -> 密码登录不可用）
        if(user.getPassword() == null || !encoder.matches(dto.getPassword(), user.getPassword())){
            throw new BusinessException("密码错误");
        }
        //3.黑名单检查（登录预检）
        if (rateLimitService.isBlocked(user.getId())) {
            throw new BusinessException("账号已被临时限制，请明天再试");
        }
        return user;
    }

    @Override
    @Transactional
    public SysUser loginByEmail(String email){
        //1.按邮箱查用户
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("email",email));
        //2.没注册 -> 自动注册（无密码，一键登录）
        if(user == null){
            user = new SysUser();
            user.setEmail(email);
            user.setUsername(generateUsername(null, email));
            user.setRole(2);
            userMapper.insert(user);
            log.info("验证码登录自动注册：{}", email);
        }
        //3.黑名单信息
        if(rateLimitService.isBlocked(user.getId())){
            throw new BusinessException("账号已被临时限制，请明天再试");
        }
        return user;
    }

    /**
     *Github登录
     */
    @Override
    @Transactional
    public SysUser loginByGithub(Long githubId, String githubLogin){
        //按Github ID查（唯一标识）
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("github_id", githubId));
        if(user == null){
            //自动注册：username用Github登录名
            user = new SysUser();
            user.setGithubId(githubId);
            user.setUsername(generateUsername(githubLogin, null));
            user.setRole(2);
            userMapper.insert(user);
            log.info("GitHub 登录自动注册：{} (id={})", githubLogin, githubId);
        }
        return user;
    }

    @Override
    public SysUser findById(Long id){
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("id", id));
        return user;
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO dto){
        //1.验证码验证（用后即焚）
        verifyCodeService.verify(dto.getEmail(), dto.getCode());
        //2.查用户
        SysUser user = userMapper.selectOne(
                new QueryWrapper<SysUser>().eq("email", dto.getEmail()));
        if(user == null){
            throw new BusinessException("该邮箱未注册");
        }
        //3.设置新密码（BCrypt）
        SysUser update = new SysUser();
        update.setId(user.getId());
        update.setPassword(encoder.encode(dto.getNewPassword()));
        userMapper.updateById(update);
        log.info("密码已重置：{}", dto.getEmail());
    }

    /**
     * 更新用户资料
     */
    @Override
    @Transactional
    public UserVO updateProfile(Long userId, ProfileDTO dto){
        //只更新非null字段
        SysUser update=new SysUser();
        update.setId(userId);
        update.setNickname(dto.getNickname());
        update.setBio(dto.getBio());
        userMapper.updateById(update);
        //返回更新后的用户
        SysUser user = userMapper.selectById(userId);
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 用户名自动生成：邮箱前缀，重复加随机数
     */
    private String generateUsername(String provided, String email){
        // 先确定基础名：用户提供的 或 邮箱前缀 或 兜底
        String base = (provided != null && !provided.isBlank())
                ? provided
                : (email != null ? email.split("@")[0] : "user");
        String name = base;
        while(userMapper.selectCount(
                new QueryWrapper<SysUser>().eq("username", name)
        ) >0 ){
            name = base + ThreadLocalRandom.current().nextInt(1000);
        }
        return name;
    }
}
