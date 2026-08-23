import { defineStore } from "pinia"

export const useUserStore = defineStore(
    'user', {
        state:() => ({
            //初始化就从localStorage读（刷新页面后还能保持登录）
            token: localStorage.getItem('token') || '',
            user: JSON.parse(localStorage.getItem('user') || 'null')
        }),
        actions: {
            //登录后成功调用：存内存 + 存 localStorage
            setLogin(token,user){
                this.token = token
                this.user = user
                localStorage.setItem('token', token)
                localStorage.setItem('user', JSON.stringify(user))
            },
            //退出登录
            logout(){
                this.token = ''
                this.user = null
                localStorage.removeItem('token')
                localStorage.removeItem('user')
            }
        }
    }
)