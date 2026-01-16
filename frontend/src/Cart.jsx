import { useEffect, useState } from "react";
import { Link } from "react-router-dom"; // For the "Continue Shopping" link
import Navbar from "./Navbar";

export default function Cart() {
    const [cart, setCart] = useState(null); // Holds the entire Cart Object
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchCart();
    }, []);

    const fetchCart = async () => {
        try {
            const token = localStorage.getItem("token");
            const userId = localStorage.getItem("userId");

            if (!userId) {
                throw new Error("User ID not found");
            }

            const response = await fetch(`http://localhost:8080/api/carts/${userId}`, {
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("Failed to fetch cart");

            const data = await response.json();
            setCart(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const removeItem = async (itemId) => {
        if (!confirm("Are you sure you want to remove this item?")) return;

        try {
            const token = localStorage.getItem("token");
            const userId = localStorage.getItem("userId");

            const response = await fetch(`http://localhost:8080/api/carts/${userId}/remove/${itemId}`, {
                method: "DELETE",
                headers: {
                    "Authorization": `Bearer ${token}`
                }
            });

            if (!response.ok) throw new Error("Failed to remove item");

            // Refresh the cart UI
            fetchCart();

        } catch (err) {
            alert(err.message);
        }
    };

    if (loading) return <div className="text-center mt-20">Loading cart...</div>;
    if (error) return <div className="text-center mt-20 text-red-500">Error: {error}</div>;

    // Handle Empty Cart
    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="min-h-screen bg-gray-100">
                <Navbar />
                <div className="text-center mt-20">
                    <h2 className="text-2xl font-bold text-gray-700">Your cart is empty</h2>
                    <Link to="/" className="mt-4 inline-block text-blue-600 hover:underline">
                        Go back to shopping
                    </Link>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <div className="max-w-4xl mx-auto p-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-6">Shopping Cart</h1>

                <div className="bg-white rounded-lg shadow-md overflow-hidden">
                    {/* Table Header */}
                    <div className="grid grid-cols-12 bg-gray-50 p-4 border-b text-gray-600 font-medium text-sm">
                        <div className="col-span-6">Product</div>
                        <div className="col-span-2 text-center">Price</div>
                        <div className="col-span-2 text-center">Quantity</div>
                        <div className="col-span-2 text-right">Actions</div>
                    </div>

                    {/* Cart Items Loop */}
                    {cart.items.map((item) => (
                        <div key={item.id} className="grid grid-cols-12 p-4 border-b items-center hover:bg-gray-50">

                            {/* Product Info */}
                            <div className="col-span-6">
                                <h3 className="font-bold text-gray-800">{item.productName}</h3>
                                {/* Note: Ensure your Backend DTO returns 'productName', otherwise use item.product.name */}
                            </div>

                            {/* Price */}
                            <div className="col-span-2 text-center text-gray-600">
                                ${item.price}
                            </div>

                            {/* Quantity */}
                            <div className="col-span-2 text-center text-gray-800 font-medium">
                                {item.quantity}
                            </div>

                            {/* Actions */}
                            <div className="col-span-2 text-right">
                                <button
                                    onClick={() => removeItem(item.id)}
                                    className="text-red-500 hover:text-red-700 text-sm font-medium"
                                >
                                    Remove
                                </button>
                            </div>
                        </div>
                    ))}

                    {/* Cart Footer / Total */}
                    <div className="p-6 bg-gray-50 flex justify-between items-center">
                        <div className="text-xl font-bold text-gray-800">
                            Total: <span className="text-blue-600">${cart.totalPrice}</span>
                        </div>

                        <button className="bg-green-600 text-white px-6 py-3 rounded-lg font-bold hover:bg-green-700 transition">
                            Checkout
                        </button>
                    </div>

                </div>
            </div>
        </div>
    );
}