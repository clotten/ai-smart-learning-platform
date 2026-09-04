import request from "./request.js";

// 密码登录（邮箱+密码）
export function login(data) {
    return request.post('/auth/login', data)
}

// 邮箱注册（需要验证码）
export function register(data) {
    return request.post('/auth/register', data)
}

// 发送邮箱验证码
export function sendCode(email) {
    return request.post('/auth/send-code', { email })
}

// 验证码登录（未注册自动注册）
export function loginByCode(data) {
    return request.post('/auth/login-by-code', data)
}

//修改/重置密码（邮箱+验证码）
export function resetPassword(data){
    return request.post('/auth/reset-paaaword', data)
}