import { test, expect } from '@playwright/test';

test('Admin can log in, access the admin panel, and create a product', async ({ page }) => {

    // Generate a unique name for this specific test run
    const uniqueProductName = `Playwright Test Product ${Date.now()}`;

    // Log in as an Admin
    await page.goto('/');
    await page.getByRole('link', { name: /login/i }).click();
    await page.getByLabel(/email/i).fill('admin@test.com');
    await page.getByLabel(/password/i).fill('admin123');
    await page.getByRole('button', { name: /sign in/i }).click();

    // Open the dropdown first
    await page.getByRole('button', { name: /account/i }).click();

    // Navigate to the Admin Panel
    await page.getByRole('link', { name: /admin panel/i }).click();
    await expect(page).toHaveURL(/.*admin/);

    // Create a New Product
    await page.getByRole('button', { name: /add new product/i }).click();
    await page.getByLabel(/name/i).fill(uniqueProductName);
    await page.getByLabel(/price/i).fill('99.99');
    await page.getByLabel(/description/i).fill('Created by an automated E2E test');
    await page.getByLabel(/stock/i).fill('100');
    await page.getByLabel(/category/i).selectOption({ label: 'Electronics' });
    await page.getByRole('button', { name: /save|create|submit/i }).click();

    // Verify the Full-Stack loop
    await page.getByRole('link', { name: /home/i }).click();
    await expect(page.getByText(uniqueProductName)).toBeVisible();

});