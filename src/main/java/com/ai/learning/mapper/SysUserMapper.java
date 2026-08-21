package com.ai.learning.mapper;

import com.ai.learning.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户Mapper：继承BaseMapper后自动拥有增删改查方法
 * （不需要写实现类，Mybatis-plus运行时会自动生成实现）
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
}
