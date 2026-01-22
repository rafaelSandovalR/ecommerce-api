import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useState, useEffect } from "react";

export default function Navbar() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [searchTerm, setSearchTerm] = useState(searchParams.get("q") || "");

    // Sync input with URL: If user clicks "Back", update the input box
    useEffect(() => {
        setSearchTerm(searchParams.get("q") || "");
    }, [searchParams]);

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    }

    const handleSearch = (e) => {
        e.preventDefault(); // Stop page refresh
        navigate(`/?q=${searchTerm}`); // Update the URL. Home.jsx will see this and fetch new data.
    };

    return (
        <nav className="bg-white shadow-md px-6 py-4 flex justify-between items-center">

            {/* Logo */}
            <Link to="/" className="text-2xl font-bold text-gray-800">
                Simple<span className="text-blue-600">Store</span>
            </Link>

            {/* Search Bar */}
            <form onSubmit={handleSearch} className="flex-1 max-w-lg mx-6 flex">
                <input
                    type="text"
                    placeholder="Search products..."
                    className="w-full border border-gray-300 px-4 py-2 rounded-l-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
                <button
                    type="submit"
                    className="bg-blue-600 text-white px-6 py-2 rounded-r-md hover:bg-blue-700 transition"
                >
                    Search
                </button>
            </form>

            {/* Links & Logout */}
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