import request from './request.js'

//当前用户完整信息
export function getMe(){
    return request.get('/user/me')
}
//更新个人信息
export function updateProfile(data){
    return request.put('/user/profile', data)
}