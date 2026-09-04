<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>🎓 AI 智能学习平台</h2>

      <el-tabs v-model="activeTab">
        <!-- ===== 密码登录 ===== -->
        <el-tab-pane label="密码登录" name="password">
          <el-form @submit.prevent>
            <el-form-item>
              <el-input v-model="loginForm.email" placeholder="邮箱" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password />
            </el-form-item>
            <el-button type="primary" style="width: 100%" :loading="loading" @click="handlePasswordLogin">
              登 录
            </el-button>
          </el-form>
        </el-tab-pane>

        <!-- ===== 验证码登录 ===== -->
        <el-tab-pane label="验证码登录" name="code">
          <el-form @submit.prevent>
            <el-form-item>
              <el-input v-model="codeForm.email" placeholder="邮箱" />
            </el-form-item>
            <el-form-item>
              <div style="display: flex; width: 100%; gap: 8px">
                <el-input v-model="codeForm.code" placeholder="6位验证码" style="flex: 1" />
                <el-button :disabled="countdown > 0 || sendingCode" :loading="sendingCode" @click="handleSendCode()">
                  {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-button type="primary" style="width: 100%" :loading="loading" @click="handleCodeLogin">
              验证码登录（未注册自动注册）
            </el-button>
          </el-form>
        </el-tab-pane>

        <!-- ===== 注册 ===== -->
        <el-tab-pane label="注册" name="register">
          <el-form @submit.prevent>
            <el-form-item>
              <el-input v-model="regForm.email" placeholder="邮箱" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.username" placeholder="用户名（可选，展示用）" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" placeholder="密码（6-20位）" show-password />
            </el-form-item>
            <el-form-item>
              <div style="display: flex; width: 100%; gap: 8px">
                <el-input v-model="regForm.code" placeholder="邮箱验证码" style="flex: 1" />
                <el-button :disabled="countdown > 0 || sendingCode" :loading="sendingCode" @click="handleSendCode(regForm.email)">
                  {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-button type="primary" style="width: 100%" :loading="loading" @click="handleRegister">
              注 册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <!-- ===== GitHub 登录 ===== -->
      <el-divider>其他登录方式</el-divider>
      <el-button style="width: 100%" @click="handleGithubLogin">
        <svg style="width: 16px; margin-right: 6px; vertical-align: middle" viewBox="0 0 16 16">
          <path fill="currentColor" d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.01 8.01 0 0016 8c0-4.42-3.58-8-8-8z"/>
        </svg>
        GitHub 登录
      </el-button>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register, sendCode, loginByCode } from '../../api/auth.js'
import { useUserStore } from '../../stores/user.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('password')
const loading = ref(false)
const countdown = ref(0)
let timer = null

const loginForm = ref({ email: '', password: '' })
const codeForm = ref({ email: '', code: '' })
const regForm = ref({ email: '', username: '', password: '', code: '' })
const sendingCode = ref(false)
// ★ GitHub 回调：URL 带 ?token= → 存起来 → 进首页
onMounted(() => {
  const token = route.query.token
  if (token) {
    userStore.setLogin(token, { username: route.query.username || 'GitHub用户' })
    ElMessage.success('GitHub 登录成功')
    router.push('/')
  }
})

// 密码登录
async function handlePasswordLogin() {
  if (!loginForm.value.email || !loginForm.value.password) {
    ElMessage.warning('请输入邮箱和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm.value)
    if (res.code === 200) {
      userStore.setLogin(res.data.token, res.data)
      ElMessage.success('登录成功')
      await router.push('/')
    }
  } finally {
    loading.value = false
  }
}

// 验证码登录
async function handleCodeLogin() {
  if (!codeForm.value.email || !codeForm.value.code) {
    ElMessage.warning('请输入邮箱和验证码')
    return
  }
  loading.value = true
  try {
    const res = await loginByCode(codeForm.value)
    if (res.code === 200) {
      userStore.setLogin(res.data.token, res.data)
      ElMessage.success('登录成功')
      await router.push('/')
    }
  } finally {
    loading.value = false
  }
}

// 注册
async function handleRegister() {
  const f = regForm.value
  if (!f.email || !f.password || !f.code) {
    ElMessage.warning('邮箱、密码、验证码必填')
    return
  }
  if (f.password.length < 6) {
    ElMessage.warning('密码至少 6 位')
    return
  }
  loading.value = true
  try {
    const res = await register(f)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'password'
      loginForm.value.email = f.email
    }
  } finally {
    loading.value = false
  }
}

// 发送验证码（60 秒倒计时防刷）
async function handleSendCode(email) {
  const target = email || codeForm.value.email || regForm.value.email
  if (!target) {
    ElMessage.warning('请先输入邮箱')
    return
  }
  sendingCode.value = true
  try{
    const res = await sendCode(target)
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请查收邮件')
      startCountdown()
    } else {
      ElMessage.error(res.message)
    }
  }finally {
    sendingCode.value = false
  }

}

function startCountdown() {
  countdown.value = 60
  if (timer) clearInterval(timer)
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

// GitHub 登录
function handleGithubLogin() {
  window.location.href = '/api/auth/github/login'
}

// 组件卸载时清理计时器
onUnmounted(() => clearInterval(timer))
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
  width: 420px;
  padding: 20px;
}
h2 {
  text-align: center;
  margin-bottom: 20px;
}
</style>