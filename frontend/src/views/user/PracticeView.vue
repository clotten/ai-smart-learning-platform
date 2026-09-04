<template>
  <div>
    <!-- 学习统计条 -->
    <el-alert v-if="stats"
              :title="`已刷 ${stats.totalCount} 题 · 答对 ${stats.correctCount} · 正确率 ${stats.accuracy}%`"
              type="success" style="margin-bottom: 16px" />

    <!-- 题目卡片列表 -->
    <el-card v-for="q in list" :key="q.id" style="margin-bottom: 16px">
      <div style="margin-bottom: 8px">
        <el-tag>{{ typeText(q.type) }}</el-tag>
        <el-tag type="info" style="margin-left: 8px">{{ q.category }}</el-tag>
      </div>
      <div style="font-size: 16px; margin: 12px 0">{{ q.content }}</div>

      <!-- 多选：复选框 -->
      <el-checkbox-group v-if="q.type === 2" v-model="answers[q.id]">
        <el-checkbox v-for="(opt, key) in parseOptions(q.options)" :key="key" :value="key"
                     style="display: block; margin: 8px 0">
          {{ key }}. {{ opt }}
        </el-checkbox>
      </el-checkbox-group>
      <!-- 单选：单选钮 -->
      <el-radio-group v-else-if="q.type === 1" v-model="answers[q.id]">
        <el-radio v-for="(opt, key) in parseOptions(q.options)" :key="key" :value="key"
                  style="display: block; margin: 8px 0">
          {{ key }}. {{ opt }}
        </el-radio>
      </el-radio-group>
      <!-- 判断：对/错 -->
      <el-radio-group v-else v-model="answers[q.id]">
        <el-radio value="对" style="margin-right: 20px">对</el-radio>
        <el-radio value="错">错</el-radio>
      </el-radio-group>

      <el-button type="primary" style="margin-top: 8px"
                 :disabled="!answers[q.id] || answers[q.id].length === 0" @click="handleSubmit(q)">
        提交答案
      </el-button>

      <!-- 判分结果 -->
      <div v-if="results[q.id]" style="margin-top: 12px">
        <el-alert
            :type="results[q.id].correct ? 'success' : 'error'"
            :title="results[q.id].correct ? '✅ 回答正确！' : '❌ 回答错误'"
            :description="'正确答案：' + results[q.id].correctAnswer" />
        <div style="margin-top: 8px; color: #666">{{ results[q.id].analysis }}</div>
        <el-button type="warning" style="margin-top: 8px"
                   :loading="aiLoading[q.id]" @click="handleAiExplain(q)">
          🤖 AI 讲解
        </el-button>
        <div v-if="aiResults[q.id]" class="ai-result">{{ aiResults[q.id] }}</div>
      </div>
    </el-card>

    <el-pagination v-model:current-page="query.pageNum" :total="total"
                   layout="total, prev, pager, next" style="margin-top: 16px" @current-change="loadData" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { pageQuestions } from '../../api/question.js'
import { submitAnswer, getStats } from '../../api/answer.js'
import { aiExplain } from '../../api/ai.js'

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 5 })
const answers = reactive({})      // 每题的选择（v-model 绑定）
const results = reactive({})      // 每题的结果
const aiResults = reactive({})    // 每题 AI 讲解
const aiLoading = reactive({})    // AI 按钮 loading
const stats = ref(null)

function typeText(t) { return { 1: '单选', 2: '多选', 3: '判断' }[t] || '' }
function parseOptions(str) {
  try { return str ? JSON.parse(str) : {} } catch { return {} }
}

async function loadData() {
  try {
    const res = await pageQuestions(query)
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
    }
  } catch (err) {
    console.error('加载题目失败', err)
  }
}

async function handleSubmit(q) {
  // 多选题：把选中的 key 拼成字符串（"AB"），后端会自动归一化排序
  try {
    const answer = Array.isArray(answers[q.id]) ? answers[q.id].join('') : answers[q.id]
    const res = await submitAnswer({ questionId: q.id, userAnswer: answer })
    if (res.code === 200) {
      results[q.id] = res.data
      loadStats()
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    console.error('提交答案失败', err)
  }
}

async function handleAiExplain(q) {
  aiLoading[q.id] = true
  try {
    const answer = Array.isArray(answers[q.id]) ? answers[q.id].join('') : answers[q.id]
    const res = await aiExplain({ questionId: q.id, userAnswer: answer })
    if (res.code === 200) {
      aiResults[q.id] = res.data.aiExplain
    } else {
      ElMessage.error(res.message)
    }
  } catch (err) {
    console.error('AI讲解失败', err)
  } finally {
    aiLoading[q.id] = false
  }
}

async function loadStats() {
  try {
    const res = await getStats()
    if (res.code === 200) stats.value = res.data
  } catch (err) {
    console.error('加载统计失败', err)
  }
}

onMounted(() => { loadData(); loadStats() })
</script>

<style scoped>
.ai-result {
  margin-top: 8px;
  padding: 12px;
  background: #fdf6ec;
  border-radius: 6px;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>