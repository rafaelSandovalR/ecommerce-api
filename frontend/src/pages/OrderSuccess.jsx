import { Link } from "react-router-dom";
import { useEffect, useState } from 'react';
import { useCart } from '../context/CartContext'

import Navbar from "../components/Navbar";

export default function OrderSuccess() {
    const { refreshCart } = useCart();
    const [isVerifying, setIsVerifying] = useState(true);

    // TODO: Consider swapping this for WebSockets solution
    useEffect(() => {
        let attempts = 0;

        const interval = setInterval(async () => {
            attempts++;
            const updatedCart = await refreshCart();

            if (updatedCart?.items.length === 0 || attempts >= 10) {
                clearInterval(interval);
                setIsVerifying(false);
            }
        }, 1000);

        // Cleanup interval if the user navigates away before it finishes
        return () => clearInterval(interval);
    }, [refreshCart]);

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <div className="max-w-2xl mx-auto p-8 mt-10 bg-white rounded-3xl shadow-md text-center">
                {isVerifying ? (
                    <div>
                        <h2 className="text-2xl font-semibold mb-4">Verifying your payment...</h2>
                        <p className="text-gray-600">Please wait while we confirm your order details.</p>
                    </div>

                ) : (
                <div>
                    <h1 className="text-3xl font-bold text-green-600 mb-4">Order Placed Successfully!</h1>
                    <p className="text-gray-600 mb-8">
                        Thank you for your purchase. Your items will be shipped to the address provided.
                    </p>
                    <Link to="/" className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition">
                        Continue Shopping
                    </Link>
                </div>
            )};
            </div>
        </div>
    );
}