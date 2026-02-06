import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { fetchCartAPI } from "../services/cartService";
import { placeOrderAPI } from "../services/orderService";
import { createPaymentIntentAPI } from "../services/paymentService";
import Navbar from "../components/Navbar";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import CheckoutForm from "../components/CheckoutForm";

const stripePromise = loadStripe("pk_test_51SxWYs7nCDsNfVCbhsiTLt7T5BgynkGR0Ni7tZ8MQEBzga3wfyxe6WP2CUTdLTYRiy6R5mwLzg49NVg5ia9wZBXc00MAGHt79u");

export default function Checkout() {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [clientSecret, setClientSecret] = useState("");

    // Form State
    const [address, setAddress] = useState("");
    const [city, setCity] = useState("");
    const [zip, setZip] = useState("");

    const navigate = useNavigate();

    useEffect(() => {
        const initData = async () => {
            try {
                const cartData = await fetchCartAPI();
                setCart(cartData);

                if (cartData && cartData.items.length > 0) {
                    const intentData = await createPaymentIntentAPI();
                    setClientSecret(intentData.clientSecret);
                }
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        initData();
    }, []);

    // Called ONLY after Stripe confims payment is successful
    const handleOrderFinalization = async (paymentId) => {
        try {
            const fullAddress = `${address}, ${city}, ${zip}`
            await placeOrderAPI(fullAddress);
            navigate("/order-success");
        } catch (err) {
            alert("Payment received, but order creation failed: " + err.message);
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

                        {/* Stripe Form */}

                        {clientSecret ? (
                            <Elements stripe={stripePromise} options={{ clientSecret }}>
                                <CheckoutForm
                                    onSuccess={handleOrderFinalization}
                                    address={address}
                                />
                            </Elements>
                        ) : (
                            <p>Loading secure payment...</p>
                        )}
                    </div>

                    {/* Right Column: Order Summary */}
                    <div className="bg-white p-6 rounded-lg shadow-md h-fit">
                        <h2 className="text-xl font-bold mb-4">Order Summary</h2>

                        <div className="space-y-2 mb-4">
                            {cart.items.map(item => (
                                <div key={item.id} className="flex justify-between text-sm">
                                    <span>{item.productName} (x{item.quantity})</span>
                                    <span>${Number(item.price * item.quantity).toFixed(2)}</span>
                                </div>
                            ))}
                        </div>

                        <div className="border-t pt-4 flex justify-between font-bold text-lg">
                            <span>Total</span>
                            <span>${Number(cart.totalPrice).toFixed(2)}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}