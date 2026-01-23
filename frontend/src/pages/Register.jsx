import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { registerAPI } from "../services/authService";

export default function Register() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    
    const handleRegister = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const data = await registerAPI(name, email, password);
            alert("Registration successful! Please login.");
            navigate("/login");
        } catch (err) {
            setError("Registration failed. Email might be taken.")
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <form onSubmit={handleRegister} className="bg-white p-8 rounded-lg shadow-lg w-96 space-y-4">
                <h2 className="text-2xl font-bold mb-6 text-center text-gray-800">Register</h2>

                {error && <p className="text-red-500 text-sm mb-4">{error}</p>}

                <input
                    type="text"
                    placeholder="Full Name"
                    className="text-sm font-medium w-full border rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                />
                <input
                    type="email"
                    placeholder="Email"
                    className="text-sm font-medium w-full border rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <input
                    type="password"
                    placeholder="Password"
                    className="text-sm font-medium w-full border rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-green-500"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button
                    type="submit"
                    className="w-full bg-green-600 text-white p-2 rounded-lg hover:bg-green-700 transition"
                >
                    Sign Up
                </button>
                <p className="pt-4 mt-4 text-sm text-center">
                    Already have an account? <Link to="/login" className="text-blue-500">Log in</Link>
                </p>
            </form>
        </div>
    );
}