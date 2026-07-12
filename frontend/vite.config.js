import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Em dev, o proxy encaminha /api para o backend (porta 8088 no host).
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: 'http://localhost:8088', changeOrigin: true }
    }
  }
});
