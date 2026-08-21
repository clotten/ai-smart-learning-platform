package com.ai.learning.common;

import lombok.Data;

/**
 * 统一返回结果：所有接口都返回这个格式
 * 前端看到 code=200 就认为成功，取data用
 */
@Data
public class Result<T> {

    private Integer code;   //状态码：200成功，其他失败
    private String message; //提示信息
    private T data;         //数据（泛型。可以是任何类型）

    // ---- 静态工厂方法：不用new，直接Result.success(...)----

    public static <T> Result<T> success(T data){
        Result<T> result=new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(){
        return success(null);
    }

    public static <T> Result<T> error(String message){
        Result<T> result=new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> error(Integer code,String message){
        Result<T> result=new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}

