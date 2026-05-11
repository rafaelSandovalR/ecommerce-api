import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { resetPasswordAPI } from '../services/authService';
import { Eye, EyeOff } from 'lucide-react';

export default function ResetPassword() {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const token = searchParams.get('token');

    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
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
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="p-8 rounded-lg shadow-lg bg-white w-96">
                <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Set New Password</h2>

                {message ?  (
                    <div className="bg-green-100 text-green-700 p-3 rounded mb-4">
                        {message} Redirecting to login...
                    </div>
                ) : (
                    <form onSubmit={handleSubmit} className="space-y-4">
                        {error && <div className="bg-red-100 text-sm text-red-700 p-3 rounded mb-4">{error}</div>}
                        <div className="relative">
                            <label className="block text-gray-600 text-sm font-medium mb-1">Set New Password</label>
                            <input
                                type={showPassword ? "text" : "password"}
                                required
                                minLength="6"
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                                type="button"
                                onClick={() => setShowPassword(!showPassword)}
                                className="absolute inset-y-11 right-0 pr-3 flex items-center text-gray-500 hover:text-gray-700"
                            >
                                {showPassword ? <EyeOff className="w-5 h-8" /> : <Eye className="w-5 h-5" />}
                            </button>
                        </div>
                        <div className="relative">
                            <label className="block text-gray-600 text-sm font-medium mb-1">Confirm New Password</label>
                            <input
                                type={showConfirmPassword ? "text" : "password"}
                                required
                                minLength="6"
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                            <button
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                className="absolute inset-y-11 right-0 pr-3 flex items-center text-gray-500 hover:text-gray-700"
                            >
                                {showConfirmPassword ? <EyeOff className="w-5 h-8" /> : <Eye className="w-5 h-5" />}
                            </button>
                        </div>
                        <button
                            type="submit"
                            disabled={isLoading || !newPassword || !confirmPassword} 
                            className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition duration-200 disabled:opacity-50"
                        >
                            {isLoading ? 'Resetting...' : "Reset Password"}
                        </button>
                    </form>
                )}
            </div>
        </div>
    );
}