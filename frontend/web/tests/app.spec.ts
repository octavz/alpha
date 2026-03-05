import { test, expect } from '@playwright/test';

test('homepage loads successfully', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  const title = page.locator('text=Alpha Directory');
  await expect(title).toBeVisible();
});

test('search box is visible', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  const searchInput = page.locator('input[placeholder*="Search"]');
  await expect(searchInput).toBeVisible();
});

test('categories section is visible', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(2000);
  const categoriesHeading = page.locator('text=Popular Categories');
  await expect(categoriesHeading).toBeVisible();
});

test('featured businesses section is visible', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  await page.waitForTimeout(2000);
  const featuredHeading = page.locator('text=Featured Businesses');
  await expect(featuredHeading).toBeVisible();
});

test('login button is visible', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  const loginButton = page.locator('[data-testid="header-login-link"]');
  await expect(loginButton).toBeVisible();
});

test('can navigate to login page', async ({ page }) => {
  await page.goto('http://localhost:5173');
  await page.waitForLoadState('networkidle');
  await page.locator('[data-testid="header-login-link"]').click();
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('[data-testid="login-title"]');
  const loginTitle = page.locator('[data-testid="login-title"]');
  await expect(loginTitle).toHaveText('Login');
});

test('registration form works', async ({ page }) => {
  await page.goto('http://localhost:5173/login');
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('[data-testid="register-link"]');
  await page.locator('[data-testid="register-link"]').click();
  await page.waitForLoadState('networkidle');
  await page.waitForSelector('[data-testid="register-title"]');
  const registerTitle = page.locator('[data-testid="register-title"]');
  await expect(registerTitle).toHaveText('Create Account');
});

test('api health check', async ({ page }) => {
  const response = await page.request.get('http://localhost:3000/api/v1/health');
  expect(response.ok()).toBeTruthy();
  const data = await response.json();
  expect(data.success).toBe(true);
});

test('api returns categories', async ({ page }) => {
  const response = await page.request.get('http://localhost:3000/api/v1/categories');
  expect(response.ok()).toBeTruthy();
  const data = await response.json();
  expect(data.success).toBe(true);
  expect(Array.isArray(data.data)).toBe(true);
});

test('api returns businesses', async ({ page }) => {
  const response = await page.request.get('http://localhost:3000/api/v1/businesses/search?size=5');
  expect(response.ok()).toBeTruthy();
  const data = await response.json();
  expect(data.success).toBe(true);
});