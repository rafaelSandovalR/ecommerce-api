import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi } from 'vitest';
import Login from './Login';

// MOCK THE CONTEXT
// Tells Vitest: Whenever Login tries to import useAuth, give it this fake version instead.
vi.mock('../context/AuthContext', () => ({
    useAuth: () => ({
        login: vi.fn(), // Dummy function
    })
}));

// Creates a block, grouping related tests
describe('Login Component', () => {

    // "it" or "test" defines a single, specific test case
    it('renders the login form correctly', () => {
        
        // RENDER THE COMPONENT
        // Wrap it in a BrowserRouter so the <Link> component doesn't crash
        render(
            <BrowserRouter>
                <Login/>
            </BrowserRouter>
        );

        // ASSERTIONS (Checking the Virtual DOM)

        // screen.getByRole targets HTML elements by their accessibility role (h1-h6 are 'heading')
        expect(screen.getByRole('heading', { name: /login/i })).toBeInTheDocument();

        // screen.getByLabelText finds the <label> and grabs the <input> connected to it
        expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/password/i)).toBeInTheDocument();

        // Finds the button
        expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
    });
});