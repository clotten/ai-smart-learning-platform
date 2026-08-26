package com.ai.learning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.http.HttpHeaders;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * GitHub OAuth2:跳转授权 -> code换token -> token换用户信息
 */
@Service
public class GitHubOAuthService {

    @Value("${app.oauth.github.client-id}")
    private  String clientId;

    @Value("${app.oauth.github.client_secret}")
    private String clientSecret;

    @Value("${app.oauth.github.redirect-uri}")
    private String redirectUri;

    private final RestClient restClient = RestClient.create();

    /**
     * 1.生成Github授权跳转地址
     */
    public String buildAuthorizeUrl(){
        return "https://github.com/login/oauth/authorize?client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=read:user";
    }
    /**
     * 2.code换access_token
     */
    @SuppressWarnings("unchecked")
    public String getAccessToken(String code){
        LinkedMultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        Map<String, Object> response = restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .header(HttpHeaders.ACCEPT, "application/json")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);
        return (String) response.get("access_token");
    }
    /**
     * 3.access_token换Github用户信息
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getUserInfo(String accessToken){
        return restClient.get()
                .uri("https://api.github.com/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);
    }

}
