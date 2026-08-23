<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>🎓 AI 智能学习平台</h2>
      <el-form :model="form" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>


<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage} from "element-plus";
import { login } from '../api/auth'
import {useUserStore} from "../stores/user";

const router = useRouter()
const userStore = useUserStore()

const form = ref({username: '', password: ''})
const loading = ref(false)

async function handleLogin(){
  if(!form.value.username || !form.value.password){
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value)
    if(res.code === 200){
      userStore.setLogin(res.data.token, res.data)
      ElMessage.success('登陆成功')
      router.push('/')
    }else{
      ElMessage.error(res.message)
    }
  } catch (err) {
    // 注意：request.js拦截器已经弹过错误提示了，这里不要再重复弹窗
    console.error('登录失败', err)
  } finally {
    loading.value = false
  }

}
</script>













<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-card {
  width: 400px;
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 20px;
}
</style>