import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import Login from './Login';

const mockLogin = vi.fn();
const mockNavigate = vi.fn();

// Mock AuthContext
// Tells Vitest: Whenever Login tries to import useAuth, give it this fake version instead.
vi.mock('../context/AuthContext', () => ({
    useAuth: () => ({
        login: mockLogin, // Dummy function
    })
}));

// Mock React Router's navigation
vi.mock('react-router-dom', async () => {
    const actual = await vi.importActual('react-router-dom'); // Keep the real BrowserRouter
    return {
        ...actual,
        useNavigate: () => mockNavigate, // But replace useNavigate with dummy fn
    };
});

// Mock the API call
// When the component calls loginAPI, it will instantly receive a fake token instead of hitting a server
vi.mock('../services/authService', () => ({
    loginAPI: vi.fn(() => Promise.resolve({ token: 'fake-jwt-token' }))
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

    it('allows a user to type and submit the form successfully', async () => {

        // Setup the ghost user
        const user = userEvent.setup();

        render(
            <BrowserRouter>
                <Login/>
            </BrowserRouter>
        );

        // Grab the elements
        const emailInput = screen.getByLabelText(/email/i);
        const passwordInput = screen.getByLabelText(/password/i);
        const submitButton = screen.getByRole('button', { name: /sign in/i });

        // Simulate Typing
        await user.type(emailInput, 'test@test.com');
        await user.type(passwordInput, 'password123');

        // Verify the React state updated the input boxes
        expect(emailInput).toHaveValue('test@test.com');
        expect(passwordInput).toHaveValue('password123');

        // Simulate clicking submit
        await user.click(submitButton);

        // Assert the success logic
        // waitFor because API calls are asynchronous
        await waitFor(() => {
            // Did it pass the fake token to our AuthContext?
            expect(mockLogin).toHaveBeenCalledWith('fake-jwt-token');
            // Did it redirect the user to the home page?
            expect(mockNavigate).toHaveBeenCalledWith('/');
        });
    });
});