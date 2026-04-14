import { resolve } from 'node:path';

import { defineConfig } from 'vite';

export default defineConfig({
  root: './src',
  resolve: {
    alias: {
      zprinter: resolve(__dirname, '../dist/esm/index.js'),
    },
  },
  build: {
    outDir: '../dist',
    minify: false,
    emptyOutDir: true,
  },
});
