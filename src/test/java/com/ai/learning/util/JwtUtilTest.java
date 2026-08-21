package com.ai.learning.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JwtUtil 单元测试：验证 token 生成 / 解析 / 防篡改
 */
@SpringBootTest //启动整个Spring环境（这样@Autowired才有东西注入）
class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 生成和解析token_应该能拿到用户数据(){
        //1.生成token
        String token = jwtUtil.generateToken(1L,"zhangsan",2);

        //2.解析token
        Claims claims = jwtUtil.parseToken(token);

        //3.断言：期望值和实际值一致
        assertEquals("1",claims.getSubject(),"用户id应该正确");
        assertEquals("zhangsan", claims.get("username"), "用户名应该正确");
        assertEquals(2, claims.get("role"), "角色应该正确");
    }

    @Test
    void 解析被篡改的token_应该抛异常() {
        String token = jwtUtil.generateToken(1L, "zhangsan", 2);

        // 把 token 末尾改几个字符，模拟被篡改
        String tampered = token.substring(0, token.length() - 5) + "xxxxx";

        // 断言：解析应该抛 JwtException
        assertThrows(io.jsonwebtoken.JwtException.class,
                () -> jwtUtil.parseToken(tampered));
    }


}
