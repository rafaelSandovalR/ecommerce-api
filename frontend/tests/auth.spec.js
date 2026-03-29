import { test, expect, request} from '@playwright/test';

test.beforeAll(async () => {
    const apiContext = await request.newContext();
    await apiContext.post('http://localhost:8080/api/auth/register', {
        data: {
            name: 'Test User',
            email: 'test@test.com',
            password: 'password123'
        }
    });
    await apiContext.dispose();
});

test('User can log in and view authenticated navigation', async ({ page }) => {
    // Navigate to the base URL
    await page.goto('/');
    // Click the Login link in the Navbar
    await page.getByRole('link', { name: /login/i }).click();
    // Wait for the URL to change to the login page
    await expect(page).toHaveURL(/.*login/);
    // Fill out the login form
    await page.getByLabel(/email/i).fill('test@test.com');
    await page.getByLabel(/password/i).fill('password123');
    // Submit the form
    await page.getByRole('button', { name: /sign in/i }).click();
    // Verify the Full-Stack integration
    await expect(page.getByRole('button', { name: /logout/i })).toBeVisible();
    await expect(page.getByRole('link', { name: /orders/i })).toBeVisible();
});