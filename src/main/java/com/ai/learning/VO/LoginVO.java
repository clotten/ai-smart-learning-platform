package com.ai.learning.VO;


import lombok.Data;

/**
 * 登录响应VO:只返回前端需要看的数据
 * 注意：没有password字段 -> 天然不会泄露密码啦
 */
@Data
public class LoginVO {
    private Long id;
    private String username;
    private String nickname;
    private Integer role;
    private String avatar;
    private String token;
}
