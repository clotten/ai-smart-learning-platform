import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import Layout from "../views/Layout.vue";

// 路由配置（后续页面陆续加）
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/',
      component: Layout,  //布局包裹所有页面
      redirect: '/question',
      children: [
        { path: 'question', component: () => import('../views/QuestionListView.vue')},
        { path: 'practice', component: () => import('../views/PracticeView.vue') },
        { path: 'leaderboard', component: () => import('../views/LeaderboardView.vue') },
        { path: 'ai', component: () => import('../views/AiChatView.vue') },
        { path: 'profile', component: () => import('../views/ProfileView.vue')}
      ]
    }
  ]
})

//路由守卫：没登录就去登录页（对应后端拦截器）
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  if(!token && to.path !== '/login') return '/login'
  if(token && to.path === '/login') return '/'
})

export default router
