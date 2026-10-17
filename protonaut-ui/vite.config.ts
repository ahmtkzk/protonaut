import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  base: './',
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: false,
    emptyOutDir: true,
  },
  server: {
    port: 3000,
    proxy: {
      '/protonaut/api': {
        target: 'http://localhost:8590',
        changeOrigin: true,
      },
    },
  },
});
