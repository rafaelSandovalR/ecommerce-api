import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { ShoppingCart, Home, User } from "lucide-react";

export default function Navbar() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const [searchTerm, setSearchTerm] = useState(searchParams.get("q") || "");
    const { user, logout } = useAuth();
    const { totalItems } = useCart();
    const isAdmin = user?.role === "ROLE_ADMIN";

    // Dropdown State & Ref
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const menuRef = useRef(null);

    // Sync input with URL
    useEffect(() => {
        setSearchTerm(searchParams.get("q") || "");
    }, [searchParams]);

    // Close dropdown when clicking outside of it
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsMenuOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const handleLogout = () => {
        logout();
        navigate("/login");
    };

    const handleSearch = (e) => {
        e.preventDefault();
        navigate(`/?q=${searchTerm}`);
    };

    return (
        <nav className="bg-white shadow-md px-6 py-4 flex justify-between items-center relative z-50">

            {/* Logo */}
            <Link to="/" className="text-2xl font-bold text-gray-800">
                Simple<span className="text-blue-600">Store</span>
            </Link>

            {/* Search Bar */}
            <form onSubmit={handleSearch} className="flex-1 max-w-lg mx-6">
                <div className="flex items-center w-full rounded-md overflow-hidden focus-within:ring-2 focus-within:ring-blue-500 focus-within:border-blue-500">
                    <input
                        type="text"
                        placeholder="Search products..."
                        className="w-full px-4 py-2 shadow-[inset_0_4px_4px_-2px_rgba(0,0,0,0.1),inset_0_-4px_4px_-2px_rgba(0,0,0,0.05),inset_4px_0_4px_-2px_rgba(0,0,0,0.05)] focus:outline-none"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                    <button
                        type="submit"
                        className="bg-blue-600 text-white px-6 py-2 hover:bg-blue-700 transition"
                    >
                        Search
                    </button>
                </div>
            </form>

            {/* Links & Icons */}
            <div className="flex items-center space-x-6">
                
                {/* Home Icon */}
                <Link to="/" className="text-gray-600 hover:text-blue-600 transition" title="Home">
                    <Home className="w-6 h-6" />
                </Link>

                {/* LOGGED IN */}
                {user ? (
                    <>
                        {/* Cart (Stays visible) */}
                        <Link to="/cart" className="text-gray-600 hover:text-blue-600 transition relative" title="Cart">
                            <ShoppingCart className="w-6 h-6" />
                            {totalItems > 0 && (
                                <span className="absolute -top-2 -right-2 bg-blue-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">
                                    {totalItems}
                                </span>
                            )}
                        </Link>

                        {/* User Account Dropdown */}
                        <div className="relative" ref={menuRef}>
                            <button
                                onClick={() => setIsMenuOpen(!isMenuOpen)}
                                className="text-gray-600 hover:text-blue-600 transition focus:outline-none flex items-center"
                                title="Account"
                            >
                                <User className="w-6 h-6" />
                            </button>

                            {/* Dropdown Menu */}
                            {isMenuOpen && (
                                <div className="absolute right-0 mt-3 w-48 bg-white rounded-md shadow-lg py-2 border border-gray-100 z-50">
                                    <div className="px-4 py-2 border-b border-gray-100 mb-1">
                                        <p className="text-sm font-medium text-gray-800 truncate">Hello,</p>
                                        <p className="text-xs text-gray-500 truncate">{user.email}</p>
                                    </div>
                                    
                                    {isAdmin && (
                                        <Link
                                            to="/admin"
                                            className="block px-4 py-2 text-sm font-bold text-red-600 hover:bg-gray-50 transition"
                                            onClick={() => setIsMenuOpen(false)}
                                        >
                                            Admin Panel
                                        </Link>
                                    )}
                                    
                                    <Link
                                        to="/orders"
                                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition"
                                        onClick={() => setIsMenuOpen(false)}
                                    >
                                        My Orders
                                    </Link>
                                    
                                    <button
                                        onClick={() => {
                                            setIsMenuOpen(false);
                                            handleLogout();
                                        }}
                                        className="block w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition mt-1 border-t border-gray-100"
                                    >
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    </>
                ) : (
                    /* LOGGED OUT */
                    <>
                        <Link to="/login" className="text-blue-600 font-medium hover:underline">
                            Login
                        </Link>
                        <Link to="/register" className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition">
                            Register
                        </Link>
                    </>
                )}
            </div>
        </nav>
    );
}