package com.ai.learning.user.vo;


import lombok.Data;

/**
 * 用户信息VO:返回给前端展示
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String bio;
    private String avatar;
    private Integer role;
}
