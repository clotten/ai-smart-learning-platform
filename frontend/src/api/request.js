import axios from "axios";
// 必须导入消息提示
import { ElMessage } from 'element-plus'
import router from '../router'

//创建 axios 实例：baseURL 用相对路径
const request = axios.create({
    baseURL: "/api",
    timeout: 30000
})

//请求拦截器：发出前自动带token（对应后端JWT拦截器）
request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if(token){
        config.headers.Authorization = 'Bearer ' + token
    }
    return config
})

//响应拦截器：接收后统一处理
request.interceptors.response.use(
    response => {
        // 从response里面取出后端返回json
        const data = response.data
        //业务错误（code !== 200）统一弹提示
        if (data && data.code !== 200) {
            ElMessage.error(data.message || '请求失败')

            //账号被限制 → 强制退出登录跳回登录页
            if (data.message && data.message.includes('临时限制')) {
                localStorage.removeItem('token')
                localStorage.removeItem('user')
                router.push('/login')             // SPA 内部导航，不刷新页
            }
            return Promise.reject(new Error(data.message))
        }
        return data
    },
    error => {
        //401 = token 失效 -> 踢回登录页
        if(error.response && error.response.status === 401){
            localStorage.removeItem('token')
            localStorage.removeItem('user')
            router.push('/login')             // SPA 内部导航，不刷新页
        }
        ElMessage.error(error.message || '网络错误')
        return Promise.reject(error)
    }
)

export default  request
