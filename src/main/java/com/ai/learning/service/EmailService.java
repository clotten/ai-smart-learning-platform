package com.ai.learning.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 邮件服务：发验证码邮件
 */
@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.smtp.from}")
    private String from;

    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    /**
     * 发送登录验证码
     */
    public void sendCode(String to,String code){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject("AI智能学习平台 - 登录验证码");
        msg.setText("您的登录验证码是：" + code + ", 5分钟内有效。\n如非本人操作，请忽略本邮件");
        mailSender.send(msg);
        log.info("验证码邮件已发送到：{}", to);
    }

}
