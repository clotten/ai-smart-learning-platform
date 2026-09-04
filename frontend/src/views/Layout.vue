<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from "element-plus";
import { useUserStore} from "../stores/user";

const router = useRouter()
const userStore = useUserStore()

function handleCommand(cmd) {
  if (cmd === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } else if (cmd === 'profile') {
    router.push('/profile')   // 以后做个人主页
  }
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">🎓 AI学习平台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/question">📚 题库</el-menu-item>
        <el-menu-item index="/practice">✍️ 刷题</el-menu-item>
        <el-menu-item index="/leaderboard">🏆 排行榜</el-menu-item>
        <el-menu-item index="/ai">🤖 AI助手</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <el-dropdown @command="handleCommand">
    <span class="user-info">
      <!-- avatar 为空 → 显示名字首字；有头像 → 显示图片 -->
      <el-avatar :size="32" :src="userStore.user?.avatar">
        {{ (userStore.user?.nickname || userStore.user?.username || 'U')[0] }}
      </el-avatar>
      <span style="margin-left: 8px">{{ userStore.user?.nickname || userStore.user?.username }}</span>
    </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">个人主页</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout { height: 100vh; }
.aside { background: #fff; border-right: 1px solid #eee; }
.logo { padding: 16px; font-size: 18px; font-weight: bold; }
.header { display: flex; justify-content: flex-end; align-items: center; background: #fff; border-bottom: 1px solid #eee; }
</style>