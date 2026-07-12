import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';

// Config dos testes de unidade do frontend (issue #5/#7). Ambiente jsdom (localStorage/
// window/DOM) + plugin React para transformar JSX nos testes de componente. Separado do
// vite.config.js (que tem o proxy de dev) para não interferir.
export default defineConfig({
  plugins: [react()],
  test: {
    environment: 'jsdom',
    globals: true,
    include: ['src/**/*.test.{js,jsx}'],
  },
});
