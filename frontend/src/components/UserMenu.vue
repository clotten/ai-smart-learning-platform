<template>
  <div class="user-menu">
    <el-dropdown @command="handleCommand">
      <span class="user-info">
        <el-avatar :size="32" :src="userStore.user?.avatar">
          {{ (userStore.user?.nickname || userStore.user?.username || 'U')[0] }}
        </el-avatar>
        <span class="username">{{ userStore.user?.nickname || userStore.user?.username }}</span>
        <el-icon><ArrowDown /></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">👤 个人主页</el-dropdown-item>
          <!-- 在管理端：显示"返回用户端" -->
          <el-dropdown-item v-if="isAdminRoute" command="home">🏠 返回用户端</el-dropdown-item>
          <!-- 在用户端且是管理员：显示"管理后台" -->
          <el-dropdown-item v-if="isAdmin && !isAdminRoute" command="admin" divided>🛠 管理后台</el-dropdown-item>
          <el-dropdown-item command="logout" divided>🚪 退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 管理员才显示"管理后台"入口
const isAdmin = computed(() => userStore.user?.role === 1)
// 当前是否在管理端页面（决定显示"返回用户端"）
const isAdminRoute = computed(() => route.path.startsWith('/admin'))

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')
  } else if (cmd === 'admin') {
    router.push('/admin/dashboard')
  } else if (cmd === 'home') {
    router.push('/')          // 返回用户端首页（刷题）
  }
}
</script>

<style scoped>
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;      /* 去掉点击边框 */
}
.username {
  font-size: 14px;
  color: #333;
}
</style>