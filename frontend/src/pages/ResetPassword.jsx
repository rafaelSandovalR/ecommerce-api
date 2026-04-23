import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { resetPasswordAPI } from '../services/authService';

export default function ResetPassword() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const token = searchParams.get('token');

    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState(null);
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (newPassword !== confirmPassword) {
            setError("Passwords do not match");
            return;
        }

        if (!token) {
            setError("Missing reset token. Please use the link from your email.");
            return;
        }

        setIsLoading(true);
        setError(null);

        try {
            const data = await resetPasswordAPI(token, newPassword);
            setMessage(data.message);
            setTimeout(() => {
                navigate('/login');
            }, 3000);
        } catch (err) {
            setError(err.message || "Failed to reset password");
        } finally {
            setIsLoading(false);
        }
    };

    if (!token) {
        return (
            <div className="text-center mt-20 text-red-600">
                <h2>Invalid request. No reset token found in the URL</h2>
            </div>
        );
    }

    return (
        <div className="max-w-md mx-auto mt-20 p-6 border rounded-lg shadow-md bg-white">
            <h2 className="text-2xl font-bold mb-4">Set New Password</h2>

            {message ?  (
                <div className="bg-green-100 text-green-700 p-3 rounded mb-4">
                    {message} Redirecting to login...
                </div>
            ) : (
                <form onSubmit={handleSubmit}>
                    {error && <div className="bg-red-100 text-red-700 p-3 rounded mb-4">{error}</div>}
                    <div className="mb-4">
                        <label className="block text-gray-700 font-bold mb-2">Set New Password</label>
                        <input
                            type="password"
                            required
                            minLength="6"
                            value={newPassword}
                            onChange={(e) => setNewPassword(e.target.value)}
                            className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 font-bold mb-2">Confirm New Password</label>
                        <input
                            type="password"
                            required
                            minLength="6"
                            value={confirmPassword}
                            onChange={(e) => setConfirmPassword(e.target.value)}
                            className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    <button
                        type="submit"
                        disabled={isLoading || !newPassword || !confirmPassword} 
                        className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                    >
                        {isLoading ? 'Resetting...' : "Reset Password"}
                    </button>
                </form>
            )}
        </div>
    )
}