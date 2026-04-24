import { useState } from 'react';
import { Link } from 'react-router-dom';
import { forgotPasswordAPI } from '../services/authService';

export default function ForgotPassword() {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setMessage(null);
        setError(null);

        try {
            const data = await forgotPasswordAPI(email);
            setMessage(data.message);
            setEmail('');
        } catch (err) {
            setError(err.message || "Failed to process request");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 bg-white shadow-lg rounded-lg w-96">
                <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Forgot Password</h2>
                <p className="text-gray-600 mb-6 text-sm text-center">Enter your email address and we will send you a link to reset your password</p>

                {message && <div className="bg-green-100 text-green-700 p-3 rounded mb-4 text-sm">{message}</div>}
                {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4 text-sm">{error}</div>}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-600 text-sm font-medium mb-1">Email</label>
                        <input
                            type="email"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={isLoading || !email}
                        className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-200 disabled:opacity-50"
                    >
                        {isLoading ? 'Sending...' : 'Send Reset Link'}
                    </button>
                </form>

                <div className="mt-6 text-center text-sm">
                    <Link to="/login" className="text-blue-500 hover:underline">Back to Login</Link>
                </div>
            </div>
        </div>
    );
}