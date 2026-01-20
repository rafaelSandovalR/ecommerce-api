import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCartAPI } from "../services/cartService";
import Navbar from "../components/Navbar";

export default function Checkout() {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Form State
    const [address, setAddress] = useState("");
    const [city, setCity] = useState("");
    const [zip, setZip] = useState("");
    const [isOrdering, setIsOrdering] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        fetchCart();
    }, []);
    
    const fetchCart = async () => {
        try {
            const data = await fetchCartAPI();
            setCart(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handlePlaceOrder = async (e) => {
        e.preventDefault();
        setIsOrdering(true);

        try {
            const token = localStorage.getItem("token");
            const fullAddress = `${address}, ${city}, ${zip}`

            const response = await fetch(`http://localhost:8080/api/orders/place`, {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    shippingAddress: fullAddress
                })
            });

            if (!response.ok) throw new Error("Failed to place order");

            navigate("/order-success");
        } catch (err) {
            alert("Order failed: " + err.message);
            setIsOrdering(false);
        }
    };

    if (loading) return <div className="text-center mt-20">Loading...</div>;
    if (error) return <div className="text-center mt-20 text-red-500">Error: {error}</div>;
    if (!cart || !cart.items || cart.items.length === 0) {
        return <div className="text-center mt-20">Cart is empty. Redirecting...</div>;
    }

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <div className="max-w-4xl mx-auto p-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-6">Checkout</h1>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">

                    {/* Left Column: Shipping & Payment Form */}
                    <div className="bg-white p-6 rounded-lg shadow-md">
                        <h2 className="text-xl font-bold mb-4">Shipping Information</h2>

                        <form onSubmit={handlePlaceOrder} id="checkout-form">
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Address</label>
                                <input
                                    type="text"
                                    required
                                    className="w-full p-2 border rounded"
                                    placeholder="123 Main St"
                                    value={address}
                                    onChange={(e) => setAddress(e.target.value)}
                                />
                            </div>

                            <div className="grid grid-cols-2 gap-4 mb-4">
                                <div>
                                    <label className="block text-gray-700 text-sm font-bold mb-2">City</label>
                                    <input
                                        type="text"
                                        required
                                        className="w-full p-2 border rounded"
                                        placeholder="New York"
                                        value={city}
                                        onChange={(e) => setCity(e.target.value)}
                                    />
                                </div>
                                <div>
                                    <label className="block text-gray-700 text-sm font-bold mb-2">Zip Code</label>
                                    <input
                                        type="text"
                                        required
                                        className="w-full p-2 border rounded"
                                        placeholder="10001"
                                        value={zip}
                                        onChange={(e) => setZip(e.target.value)}
                                    />
                                </div>
                            </div>

                            <h2 className="text-xl font-bold mb-4 mt-8">Payment Details</h2>
                            {/* Mock Payment Fields */}
                            <div className="mb-4">
                                <label className="block text-gray-700 text-sm font-bold mb-2">Card Number</label>
                                <input type="text" className="w-full p-2 border rounded" placeholder="0000 0000 0000 0000"/>
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                <label className="block text-gray-700 text-sm font-bold mb-2">Expiry</label>
                                <input type="text" className="w-full p-2 border rounded" placeholder="MM/YY"/>                                    
                                </div>
                                <div>
                                <label className="block text-gray-700 text-sm font-bold mb-2">CVC</label>
                                <input type="text" className="w-full p-2 border rounded" placeholder="123"/>                                    
                                </div>
                            </div>
                        </form>
                    </div>

                    {/* Right Column: Order Summary */}
                    <div className="bg-white p-6 rounded-lg shadow-md h-fit">
                        <h2 className="text-xl font-bold mb-4">Order Summary</h2>

                        <div className="space-y-2 mb-4">
                            {cart.items.map(item => (
                                <div key={item.id} className="flex justify-between text-sm">
                                    <span>{item.productName} (x{item.quantity})</span>
                                    <span>${item.price * item.quantity}</span>
                                </div>
                            ))}
                        </div>

                        <div className="border-t pt-4 flex justify-between font-bold text-lg">
                            <span>Total</span>
                            <span>${cart.totalPrice}</span>
                        </div>

                        <button
                            type="submit"
                            form="checkout-form"
                            disabled={isOrdering}
                            className={`w-full mt-6 py-3 rounded-lg font-bold text-white transition ${
                                isOrdering ? "bg-gray-400" : "bg-green-600 hover:bg-green-700"
                            }`}
                        >
                            {isOrdering ? "Processing..." : "Place Order"}
                        </button>
                    </div>
                </div>
            </div>

        </div>
    );
}