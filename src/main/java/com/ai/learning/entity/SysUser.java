package com.ai.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
/**
 * 用户实体类：对应数据库 sys_user表
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO) //主键，数据库自增
    private Long id;

    @NotBlank(message = "用户名不能为空")
    private String username;    //用户名

    @NotBlank(message = "密码不能为空")
    private String password;    //BCrypt加密后的密码

    private String nickname;    //昵称

    private Integer role;       //角色：1管理员 2学生

    private String avatar;      //头像

    private LocalDateTime createdAt;    //创建时间

    private LocalDateTime updatedAt;    //更新时间

    @TableLogic
    private Integer deleted;    //逻辑删除（查自动过滤deleted=0）

}
