import { Link, useNavigate } from "react-router-dom";

export default function Navbar() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <nav className="bg-white shadow-md px-6 py-4 flex justify-between items-center">
            <Link to="/" className="text-2xl font-bold text-gray-800">
                Simple<span className="text-blue-600">Store</span>
            </Link>

            <div className="flex items-center space-x-6">
                <Link to="/" className="text-gray-600 hover:text-blue-600 font-medium transition">
                    Home
                </Link>
                <Link to="/cart" className="text-gray-600 hover:text-blue-600 font-medium transition">
                    Cart
                </Link>
                <Link to="/orders" className="text-gray-600 hover:text-blue-600 font-medium transition">
                    Orders
                </Link>

                <button
                    onClick={handleLogout}
                    className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition"
                >
                    Logout
                </button>
            </div>
        </nav>
    );
}