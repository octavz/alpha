import { test, expect } from '@playwright/test';

test.describe('Alpha Business Directory', () => {
  test('homepage loads successfully', async ({ page }) => {
    await page.goto('http://localhost:5173');
    
    // Wait for the page to load
    await page.waitForLoadState('networkidle');
    
    // Check title or main heading
    const title = page.locator('text=Alpha Directory');
    await expect(title).toBeVisible();
  });

  test('search box is visible', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');
    
    // Check search input exists
    const searchInput = page.locator('input[placeholder*="Search"]');
    await expect(searchInput).toBeVisible();
  });

  test('categories section is visible', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');
    
    // Wait for categories to load from API
    await page.waitForTimeout(2000);
    
    // Check categories heading
    const categoriesHeading = page.locator('text=Popular Categories');
    await expect(categoriesHeading).toBeVisible();
  });

  test('featured businesses section is visible', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');
    
    // Wait for businesses to load from API
    await page.waitForTimeout(2000);
    
    // Check featured businesses heading
    const featuredHeading = page.locator('text=Featured Businesses');
    await expect(featuredHeading).toBeVisible();
  });

  test('login button is visible', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');
    
    // Check login button in header
    const loginButton = page.locator('text=Login').first();
    await expect(loginButton).toBeVisible();
  });

  test('can navigate to login page', async ({ page }) => {
    await page.goto('http://localhost:5173');
    await page.waitForLoadState('networkidle');
    
    // Click login button
    await page.locator('text=Login').click();
    
    // Should see login form
    await page.waitForTimeout(1000);
    const loginForm = page.locator('text=Welcome back');
    await expect(loginForm).toBeVisible({ timeout: 5000 });
  });

  test('registration form works', async ({ page }) => {
    await page.goto('http://localhost:5173/login');
    await page.waitForLoadState('networkidle');
    
    // Click register link
    await page.locator('text=Register').click();
    
    // Should see registration form
    await page.waitForTimeout(1000);
    const registerForm = page.locator('text=Create Account');
    await expect(registerForm).toBeVisible({ timeout: 5000 });
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
});
