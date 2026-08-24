<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <template #header>我的排名</template>
      <div v-if="myRank" style="font-size: 16px">
        🏅 第 <b>{{ myRank.rank + 1 }}</b> 名 · 刷题 {{ myRank.count }} 题
        <span v-if="myRank.rank === -1" style="color: #999">（还没上榜，快去刷题！）</span>
      </div>
    </el-card>

    <el-table :data="list" stripe>
      <el-table-column label="排名" width="80">
        <template #default="{ $index }">
          <span :style="rankStyle($index)">{{ $index + 1 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户" />
      <el-table-column prop="count" label="刷题数" />
    </el-table>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getTop, getMyRank } from '../api/leaderboard'

const list = ref([])
const myRank = ref(null)

function rankStyle(i) {
  if (i === 0) return { color: '#E6A23C', fontWeight: 'bold', fontSize: '18px' }  // 金
  if (i === 1) return { color: '#909399', fontWeight: 'bold' }                    // 银
  if (i === 2) return { color: '#B87333', fontWeight: 'bold' }                    // 铜
  return {}
}

onMounted(async () => {
    //两个请求同时发送,每个接口各自捕获异常，错误不会影响其他接口
    const [topRes, myRankRes] = await Promise.all([
      getTop(10).catch(
          e => {
            console.error('榜单加载失败',e)
            return null
          }
      ),
      getMyRank().catch(e => {
        console.error('个人排名加载失败',e)
        return null
      })
    ])

    if (topRes.code === 200) list.value = topRes.data

    if (myRankRes.code === 200) myRank.value = myRankRes.data

})
</script>