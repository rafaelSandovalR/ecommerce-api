import { Link } from "react-router-dom";
import Navbar from "./Navbar";

export default function OrderSuccess() {
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-2xl mx-auto p-8 mt-10 bg-white rounded-3xl shadow-md text-center">
                <h1 className="text-3xl font-bold text-green-600 mb-4">Order Placed Successfully!</h1>
                <p className="text-gray-600 mb-8">
                    Thank you for your purchase. Your items will be shipped to the address provided.
                </p>
                <Link to="/" className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition">
                    Continue Shopping
                </Link>
            </div>
        </div>
    );
}