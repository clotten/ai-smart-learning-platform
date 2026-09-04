<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">🎓 AI学习平台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/practice">✍️ 刷题</el-menu-item>
        <el-menu-item index="/leaderboard">🏆 排行榜</el-menu-item>
        <el-menu-item index="/ai">🤖 AI助手</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header"><UserMenu /></el-header>
      <el-main>
        <!-- 面包屑 -->
        <el-breadcrumb separator="/" style="margin-bottom: 16px">
          <el-breadcrumb-item :to="{ path: '/practice' }">🎓 AI学习平台</el-breadcrumb-item>
          <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
        </el-breadcrumb>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.header {
  display: flex;
  justify-content: flex-end;    /* ★ 内容靠右 */
  align-items: center;
  border-bottom: 1px solid #eee;
}
</style>


<script setup>
import UserMenu from '../components/UserMenu.vue'   // 头像下拉（抽取公共）
</script>