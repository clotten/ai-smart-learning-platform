package com.ai.learning.common;

/**
 * 业务异常：业务逻辑出错时抛出（比如“用户名已存在”、“密码错误”）
 * 抛出去后由GlobalExceptionHandler统一捕获，不用自己try-catch
 */
public class BusinessException extends RuntimeException{
    public BusinessException(String message){
        super(message); //把错误信息传给父类，getMessage（）能拿到
    }
}
