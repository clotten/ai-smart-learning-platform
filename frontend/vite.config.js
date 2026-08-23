import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 配置
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    // 开发代理：/api 开头的请求转发到后端 8081
    // 好处：前端写 /api/xxx 就行，浏览器看到的都是同源 → 自动解决跨域！
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
