<template>
  <div class="profile">
    <el-card style="max-width: 600px">
      <template #header>👤 个人主页</template>
      <div class="info">
        <el-avatar :size="64" :src="user.avatar">
          {{ (user.nickname || user.username || 'U')[0] }}
        </el-avatar>
        <div style="margin-left: 16px">
          <div style="font-size: 18px; font-weight: bold">{{ user.nickname || user.username }}</div>
          <div style="color: #999; font-size: 13px">{{ user.email }}</div>
          <div style="margin-top: 6px; color: #666">{{ user.bio || '这个人很懒，什么都没写~' }}</div>
        </div>
      </div>
      <el-divider />
      <el-button type="primary" @click="openEdit">✏️ 编辑资料</el-button>
      <el-button type="warning" @click="pwdVisible = true">🔒 修改密码</el-button>
    </el-card>

    <!-- 编辑资料 -->
    <el-dialog v-model="editVisible" title="编辑资料" width="400px">
      <el-form :model="editForm" label-width="60px">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" maxlength="20" /></el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.bio" type="textarea" maxlength="50" show-word-limit />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码（邮箱验证码）-->
    <el-dialog v-model="pwdVisible" title="修改密码" width="400px">
      <el-form :model="pwdForm" label-width="60px">
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-20位" />
        </el-form-item>
        <el-form-item label="验证码">
          <div style="display:flex;gap:8px;width:100%">
            <el-input v-model="pwdForm.code" placeholder="发到注册邮箱" />
            <el-button :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? countdown + 's' : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwd">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMe, updateProfile } from '../api/user'
import { sendCode, resetPassword } from '../api/auth'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const user = ref({})
const editVisible = ref(false)
const pwdVisible = ref(false)
const editForm = reactive({ nickname: '', bio: '' })
const pwdForm = reactive({ newPassword: '', code: '' })
const countdown = ref(0)
let timer = null

async function load() {
  const res = await getMe()
  if (res.code === 200) user.value = res.data
}
onMounted(load)

function openEdit() {
  editForm.nickname = user.value.nickname || ''
  editForm.bio = user.value.bio || ''
  editVisible.value = true
}

async function handleEdit() {
  const res = await updateProfile(editForm)
  if (res.code === 200) {
    user.value = res.data
    userStore.user = res.data          // 同步顶栏显示！
    ElMessage.success('已保存')
    editVisible.value = false
  }
}

async function handleSendCode() {
  const res = await sendCode(user.value.email)
  if (res.code === 200) {
    ElMessage.success('验证码已发送，请查收邮箱')
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } else {
    ElMessage.error(res.message)
  }
}

async function handleResetPwd() {
  if (!pwdForm.newPassword || !pwdForm.code) return ElMessage.warning('请填写完整')
  const res = await resetPassword({
    email: user.value.email,
    code: pwdForm.code,
    newPassword: pwdForm.newPassword
  })
  if (res.code === 200) {
    ElMessage.success('密码修改成功')
    pwdVisible.value = false
    pwdForm.newPassword = ''
    pwdForm.code = ''
  }
}

onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.profile { display: flex; justify-content: center; }
.info { display: flex; align-items: center; padding: 8px 0; }
</style>