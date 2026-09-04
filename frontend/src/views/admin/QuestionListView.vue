<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { pageQuestions, addQuestion, deleteQuestion } from "../../api/question.js";

const list = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, type: null, category: '', keyword: ''})
const dialogVisible = ref(false)
const form = reactive({ type: 1, category: '', content: '',options: '', answer: '', analysis: ''})

const queryLoading = ref(false)   // 查询按钮 loading
const deletingId = ref(null)      // 正在删除的题目 id

function typeText(t){
  return { 1: '单选', 2: '多选', 3: '判断'}[t] || '未知'
}

async function loadData(){
  queryLoading.value = true
  try {
    const res = await pageQuestions(query)
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
    }
  } catch (err) {
    // axios拦截器已经弹过ElMessage提示，这里只打印日志即可，不用重复弹窗
    console.error('加载题目失败', err)
  } finally {
    queryLoading.value = false
  }
}


function resetQuery(){
  query.type = null
  query.category = ''
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

// 提交前校验
function validateForm() {
  if (!form.content || !form.answer) {
    ElMessage.warning('题干和答案必填')
    return false
  }
  // 单选/多选：校验 options 必须是合法 JSON
  if (form.type !== 3 && form.options) {
    try {
      JSON.parse(form.options)
    } catch {
      ElMessage.error('选项必须是合法 JSON，如 {"A":"选项A"}')
      return false
    }
  }
  return true
}

async function handleAdd(){
  if(!validateForm()) return
  const res = await addQuestion(form)
  if(res.code === 200){
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  }else{
    ElMessage.error(res.message)
  }
}

async function handleDelete(row){
  await ElMessageBox.confirm('确定删除该题目吗？', '提示', { type: 'warning' })
  deletingId.value = row.id
  try {
    const res = await deleteQuestion(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    }
  } finally {
    deletingId.value = null
  }
}

// 重置表单（弹窗每次打开都清空）
function resetForm() {
  form.type = 1
  form.category = ''
  form.content = ''
  form.options = ''
  form.answer = ''
  form.analysis = ''
}

// 打开弹窗：先重置
function openDialog() {
  resetForm()
  dialogVisible.value = true
}

onMounted(loadData)
</script>

<template>
  <div>
    <!-- 筛选栏 -->
    <el-form inline>
      <el-form-item label="题型">
        <el-select v-model="query.type" placeholder="全部" clearable style="width: 120px">
          <el-option label="单选" :value="1" />
          <el-option label="多选" :value="2" />
          <el-option label="判断" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="分类">
        <el-input v-model="query.category" placeholder="如 Java基础" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item label="关键词">
        <el-input v-model="query.keyword" placeholder="搜索题干" clearable style="width: 180px" />
      </el-form-item>
      <el-button type="primary" :loading="queryLoading" @click="loadData">查询</el-button>
      <el-button @click="resetQuery">重置</el-button>
      <el-button type="success" @click="openDialog">＋ 新增题目</el-button>
    </el-form>

    <!-- 列表 -->
    <el-table :data="list" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="题型" width="80">
        <template #default="{ row }">{{ typeText(row.type) }}</template>
      </el-table-column>
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="content" label="题干" show-overflow-tooltip />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link type="danger" :loading="deletingId === row.id" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
        v-model:current-page="query.pageNum"
        :total="total"
        layout="total, prev, pager, next"
        style="margin-top: 16px"
        @current-change="loadData"
    />

    <!-- 新增对话框 -->
    <el-dialog v-model="dialogVisible" title="新增题目" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="题型">
          <el-radio-group v-model="form.type">
            <el-radio :value="1">单选</el-radio>
            <el-radio :value="2">多选</el-radio>
            <el-radio :value="3">判断</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="题干"><el-input v-model="form.content" type="textarea" /></el-form-item>
        <el-form-item label="选项">
          <el-input v-model="form.options" type="textarea" placeholder='JSON格式: {"A":"选项A","B":"选项B"}' />
        </el-form-item>
        <el-form-item label="答案"><el-input v-model="form.answer" placeholder="如 B / AB / 对" /></el-form-item>
        <el-form-item label="解析"><el-input v-model="form.analysis" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>

</style>