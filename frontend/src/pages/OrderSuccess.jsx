import { Link } from "react-router-dom";
import { useEffect, useState } from 'react';
import { useCart } from '../context/CartContext'
import Navbar from "../components/Navbar";

export default function OrderSuccess() {
    const { refreshCart } = useCart();
    const [isVerifying, setIsVerifying] = useState(true);
    const [isError, setIsError] = useState(false);

    // TODO: Consider swapping this for WebSockets solution
    useEffect(() => {
        let attempts = 0;

        const interval = setInterval(async () => {
            attempts++;
            const updatedCart = await refreshCart();

            if (updatedCart?.items.length === 0) {
                clearInterval(interval);
                setIsVerifying(false);
                setIsError(false);
            } else if (attempts >= 10) {
                clearInterval(interval);
                setIsVerifying(false);
                setIsError(true);
            }
        }, 3000);

        // Cleanup interval if the user navigates away before it finishes
        return () => clearInterval(interval);
    }, [refreshCart]);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <div className="max-w-2xl mx-auto p-8 mt-10 bg-white rounded-3xl shadow-md text-center">
                {isVerifying ? (
                    /* STATE 1: LOADING */
                    <div>
                        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
                        <h2 className="text-2xl font-semibold mb-4">Verifying your payment...</h2>
                        <p className="text-gray-600">Please wait while we confirm your order details.</p>
                    </div>

                ) : isError? (
                    /* STATE 2: ERROR / TIMEOUT */
                    <div>
                        <h1 className="text-2xl font-medium text-red-600 mb-4">Verification Taking Longer Than Expected</h1>
                        <p className="text-gray-500 mb-8 mx-10">
                            We haven't received confirmation from the payment provider yet.
                            Don't worry - If you received a Stripe receipt, your order is safe.
                        </p>
                        <div className="flex flex-col gap-4 w-64 mx-auto">
                            <button
                                onClick={() => window.location.reload()}
                                className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition"
                            >
                                Check Status Again
                            </button>
                            <Link to="/orders" className="text-blue-600 hover:underline">
                                View My Orders
                            </Link>
                        </div>
                    </div>
                ) : (
                    /* STATE 3: SUCCESS */
                    <div>
                        <h1 className="text-3xl font-bold text-green-600 mb-4">Order Placed Successfully!</h1>
                        <p className="text-gray-600 mb-8">
                            Thank you for your purchase. Your items will be shipped to the address provided.
                        </p>
                        <Link to="/" className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition">
                            Continue Shopping
                        </Link>
                    </div>
            )}
            </div>
        </div>
    );
}