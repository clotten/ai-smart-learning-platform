import axios from "axios";

//创建 axios 实例：baseURL 用相对路径
const request = axios.create({
        baseURL: "/api",
        timeout: 30000
    })

//请求拦截器：发出前自动带token（对应后端JWT拦截器）
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if(token){
        config.headers.Authorization = 'Bearer' + token
    }
    return config
})

//响应拦截器：接收后统一处理
request.interceptors.response.use(
    response => response.data, //后端返回｛code，message，data｝，直接拿body
    error => {
        //401 = token 失效 -> 踢回登录页
        if(error.response && error.response.status === 401){
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export default  request