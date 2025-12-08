import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // コンテナ外からのアクセスを許可
    port: 5173,
    watch: {
      usePolling: true // Dockerボリュームでのファイル監視
    },
    hmr: {
      overlay: true // エラーオーバーレイを有効化
    }
  }
})
