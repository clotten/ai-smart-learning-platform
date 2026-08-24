<template>
  <div class="chat-page">
    <div class="chat-box">
      <div v-for="(msg, i) in messages" :key="i" :class="['msg', msg.role]">
        <div class="bubble">{{ msg.content }}</div>
      </div>
      <div v-if="loading" class="msg assistant"><div class="bubble">思考中...</div></div>
    </div>
    <div class="input-bar">
      <el-input v-model="input" placeholder="问我任何编程问题..." @keyup.enter="send" />
      <el-button type="primary" :loading="loading" @click="send">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage} from "element-plus";

const messages = ref([])
const input = ref('')
const loading = ref(false)

async function send() {
  const text = input.value.trim()

  if(!text){
    ElMessage.warning('请输入内容')     //空消息提示
    return
  }
  if (loading.value) return    // 防重复发送
  input.value = ''
  messages.value.push({ role: 'user', content: text })
  loading.value = true

  const token = localStorage.getItem('token')
  let buffer = ''
  let aiText = ''
  try {
    // 用 fetch 流式读取（EventSource 不能带 Authorization 头）
    const resp = await fetch(`/api/ai/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + token
      },
      body: JSON.stringify({ message: text })
    })
    //非 200 响应 → 解析错误信息并抛出（让 catch 显示出来）
    if (!resp.ok) {
      const err = await resp.json().catch(() => ({}))
      throw new Error(err.message || '请求失败(' + resp.status + ')')
    }
    const reader = resp.body.getReader()
    const decoder = new TextDecoder()
    messages.value.push({ role: 'assistant', content: '' })

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })

      // SSE 协议：事件用 \n\n 分隔，每行是 "data: {...}"
      let idx
      while ((idx = buffer.indexOf('\n\n')) !== -1) {
        const event = buffer.slice(0, idx).trim()
        buffer = buffer.slice(idx + 2)
        if (event.startsWith('data:')) {
          const data = event.slice(5).trim()
          if (!data) continue
          try {
            const parsed = JSON.parse(data)
            //如果是错误事件，直接显示并停止
            if(parsed.error){
              messages.value[messages.value.length -1].content = parsed.error
              aiText = parsed.error
              break
            }
            aiText += parsed.content || ''
            messages.value[messages.value.length - 1].content = aiText  // 打字机！
          } catch { /* 忽略解析失败的分段 */ }
        }
      }
    }
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '出错了：' + e.message })
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.chat-page { display: flex; flex-direction: column; height: calc(100vh - 120px); }
.chat-box { flex: 1; overflow-y: auto; padding: 16px; background: #fafafa; border-radius: 8px; }
.msg { margin-bottom: 12px; display: flex; }
.msg.user { justify-content: flex-end; }
.msg.assistant { justify-content: flex-start; }
.bubble { max-width: 70%; padding: 10px 14px; border-radius: 8px; line-height: 1.6; white-space: pre-wrap; }
.msg.user .bubble { background: #409eff; color: #fff; }
.msg.assistant .bubble { background: #fff; border: 1px solid #eee; }
.input-bar { display: flex; gap: 8px; margin-top: 12px; }
</style>