<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">🛠 管理后台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/admin/dashboard">📊 数据看板</el-menu-item>
        <el-menu-item index="/admin/users">👥 用户管理</el-menu-item>
        <el-menu-item index="/admin/questions">📚 题库管理</el-menu-item>
        <el-menu-item index="/admin/config">⚙️ 配置管理</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header"><UserMenu /></el-header>
      <el-main>
        <!-- 面包屑 -->
        <el-breadcrumb separator="/" style="margin-bottom: 16px">
          <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">🛠 管理后台</el-breadcrumb-item>
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
import UserMenu from '../components/UserMenu.vue'
</script>