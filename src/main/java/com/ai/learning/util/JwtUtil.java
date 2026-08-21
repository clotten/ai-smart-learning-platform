package com.ai.learning.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类：生成token、解析token
 */
@Component
public class JwtUtil {

    //从applicarion.yml读取配置（app.jwt.secret / app.jwt.expire-minutes）
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expire-minutes}")
    private Long expireMinutes;

    /**
     * 生成token：登陆成功后调用
     */
    public String generateToken(Long userId,String username,Integer role){
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expire = new Date(now.getTime()+expireMinutes*60*1000);    //过期时间

        return Jwts.builder()
                .subject((String.valueOf(userId)))  //1.主题：用户ID
                .claim("username",username)      //2.自定义信息
                .claim("role",role)              //3.自定义信息
                .issuedAt(now)                      //签发时间
                .expiration(expire)                 //过期时间
                .signWith(key)                      //用密钥签名（防篡改）
                .compact();                         //生成最终字符串
    }

    /**
     * 解析token：拦截器里校验时调用
     * token过期或被篡改会抛异常，交给全局异常处理
     */
    public Claims parseToken(String token){
        SecretKey key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)            //用同一个密钥验证签名
                .build()
                .parseSignedClaims(token)   //解析
                .getPayload();              //取出内容（userID/username/role）
    }
}
