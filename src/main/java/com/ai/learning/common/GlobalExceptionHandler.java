package com.ai.learning.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器：所有Controller抛出的异常都在这里统一“接住”
 * 好处：接口里不用谢try-catch，错误信息格式统一
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //1.业务异常（我们自己抛的）
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e){
        log.warn("业务异常:{}",e.getMessage());
        return Result.error(e.getMessage());
    }

    //2. JWT 异常（token 过期 / 格式错误 / 被篡改）
    @ExceptionHandler(io.jsonwebtoken.JwtException.class)
    public Result<Void> handleJwtException(io.jsonwebtoken.JwtException e){
        log.warn("JWT 校验失败:{}",e.getMessage());
        return Result.error(401,"登录已过期或身份无效，请重新登录");
    }
    //3. 参数校验异常（@Valid校验不通过时Spring自动抛）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e){
        String message=e.getBindingResult().getFieldError() != null ? e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        return Result.error(message);
    }

    //4.兜底异常（意料之外的错误，防止堆栈信息泄露给前端）
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        log.error("系统异常",e);
        return Result.error(500,"系统繁忙，请稍后再试");
    }

}
