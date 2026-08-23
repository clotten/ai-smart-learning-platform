<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from "element-plus";
import { useUserStore} from "../stores/user";

const router = useRouter()
const userStore = useUserStore()

function handleLogout(){
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
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
        <span>你好，{{ userStore.user?.username }}</span>
        <el-button link type="danger" @click="handleLogout">退出登录</el-button>
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