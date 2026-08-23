import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

// 路由配置（后续页面陆续加）
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', redirect: '/login' }
  ]
})

export default router
