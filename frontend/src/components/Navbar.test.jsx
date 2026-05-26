import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import Navbar from './Navbar';
import { AuthContext } from '../context/AuthContext';
import { CartContext } from '../context/CartContext';

// CUSTOM WRAPPER
// Allows injection of any fake user or cart data into the Navbar
const customRender = (
    ui,
    {
        providerProps: { authValue, cartValue }
    }
) => {
    return render(
        <AuthContext.Provider value={authValue}>
            <CartContext.Provider value={cartValue}>
                <BrowserRouter>
                    {ui}
                </BrowserRouter>
            </CartContext.Provider>
        </AuthContext.Provider>
    );
};

describe('Navbar Component', () => {

    it('Scenario A: Renders Guest Links when no user is logged in', () => {
        // ARRANGE: Set up the fake context values for a logged-out user
        const authValue = { user: null, logout: vi.fn() };
        const cartValue = { totalItems: 0 };

        // ACT
        customRender(<Navbar />, { providerProps: { authValue, cartValue } });

        // ASSERT
        // What should a guest see?
        expect(screen.getByRole('link', { name: /login/i })).toBeInTheDocument();
        expect(screen.getByRole('link', { name: /register/i })).toBeInTheDocument();

        // They should NOT see the logout button or the admin panel
        expect(screen.queryByRole('button', { name: /log out/i })).not.toBeInTheDocument();
        expect(screen.queryByRole('link', { name: /admin panel/i })).not.toBeInTheDocument();
    });

    it('Scenario B: Renders User Links and Cart Badge when logged in', async () => {
        // ARRANGE
        const user = userEvent.setup();
        const authValue = { user: { role: 'ROLE_USER', email: 'user@test.com' }, logout: vi.fn() };
        const cartValue = { totalItems: 3 };

        // ACT
        customRender(<Navbar />, { providerProps: { authValue, cartValue } });

        // Check for the cart badge
        expect(screen.getByText('3')).toBeInTheDocument();

        // Open the user dropdown
        const accountButton = screen.getByRole('button', { name: /account/i });
        await user.click(accountButton);

        // ASSERT
        expect(screen.getByRole('button', { name: /log out/i })).toBeInTheDocument();
        expect(screen.getByRole('link', { name: /orders/i })).toBeInTheDocument();

        // Standard users should NOT see the admin panel link
        expect(screen.queryByRole('link', { name: /admin panel/i })).not.toBeInTheDocument();
    });

    it('Scenario C: Renders the Admin Panel link only for Admins', async () => {
        // ARRANGE
        const user = userEvent.setup();
        const authValue = { user: { role: 'ROLE_ADMIN', email: 'admin@test.com' }, logout: vi.fn() };
        const cartValue = { totalItems: 0 };

        // ACT
        customRender(<Navbar />, { providerProps: { authValue, cartValue } });

        // Open teh user dropdonw
        const accountButton = screen.getByRole('button', { name: /account/i });
        await user.click(accountButton);

        // ASSERT: Admin user SHOULD see the admin panel link
        expect(screen.getByRole('link', { name: /admin panel/i })).toBeInTheDocument();
    });

    it('Scenario D: Renders Logout Confirmation Modal', async () => {
        const user = userEvent.setup();
        const authValue = { user: {role: 'ROLE_USER', email: 'user@test.com'}, logout: vi.fn() };
        const cartValue = { totalItems: 0 };

        customRender(<Navbar />, { providerProps: { authValue, cartValue } });

        // ACT: Open dropdown and click the first logout button
        const accountButton = screen.getByRole('button', { name: /account/i });
        await user.click(accountButton);
        const logoutButton = screen.getByRole('button', { name: /log out/i });
        await user.click(logoutButton);

        // ASSERT: Modal appears
        expect(screen.getByText('Ready to leave?')).toBeInTheDocument();

        // ACT: Click the final "Log Out" button isnide the modal
        const confirmLogoutButton = screen.getByRole('button', { name: /log out/i });
        await user.click(confirmLogoutButton);

        // ASSERT: Verify the logout function from context was actually triggered
        expect(authValue.logout).toHaveBeenCalledTimes(1);
    });
});