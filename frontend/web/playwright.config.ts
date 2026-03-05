import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: 'C:/work/alpha/frontend/web/tests',
  testMatch: 'app.spec.ts',
  timeout: 30000,
  use: {
    headless: true,
    viewport: { width: 1280, height: 720 },
  },
  projects: [
    {
      name: 'chromium',
      use: { browserName: 'chromium' },
    },
  ],
});