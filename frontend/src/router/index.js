import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/user/LoginView.vue'
import UserLayout from "../layouts/UserLayout.vue";
import AdminLayout from "../layouts/AdminLayout.vue";

// 路由配置（后续页面陆续加）
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', component: UserLayout,  //布局包裹所有页面
      children: [
        { path: 'practice', meta: { title: '刷题' }, component: () => import('../views/user/PracticeView.vue') },
        { path: 'leaderboard', meta: { title: '排行榜' }, component: () => import('../views/user/LeaderboardView.vue') },
        { path: 'ai', meta: { title: 'AI助手' }, component: () => import('../views/user/AiChatView.vue') },
        { path: 'profile', meta: { title: '个人主页' }, component: () => import('../views/user/ProfileView.vue') }
      ]
    },
    {
      path: '/admin', component: AdminLayout,
      meta: { requiresAdmin: true },
      children: [
        { path: 'dashboard', meta: { title: '数据看板' }, component: () => import('../views/admin/DashboardView.vue') },
        { path: 'users', meta: { title: '用户管理' }, component: () => import('../views/admin/UserManageView.vue') },
        { path: 'questions', meta: { title: '题库管理' }, component: () => import('../views/admin/QuestionListView.vue') },
        { path: 'config', meta: { title: '配置管理' }, component: () => import('../views/admin/ConfigView.vue') },
      ]
    }
  ]
})

//路由守卫：没登录就去登录页（对应后端拦截器）
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if(!token && to.path !== '/login') return '/login'
  if(token && to.path === '/login') return '/'
  if(to.meta.requiresAdmin && user?.role !== 1) return '/'
})

export default router
