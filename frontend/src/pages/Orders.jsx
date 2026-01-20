import { useEffect, useState } from "react";
import { fetchOrdersAPI } from "../services/orderService";
import Navbar from "../components/Navbar";

export default function Orders() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        handleFetchOrders();
    }, []);

    const handleFetchOrders = async () => {

        try {
            const data = await fetchOrdersAPI();
            setOrders(data.content || []);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <div className="text-center mt-20">Loading orders...</div>;
    if (error) return <div className="text-center mt-20 text-red-500">Error: {error}</div>

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />

            <div className="max-w-4xl mx-auto p-8">
                <h1 className="text-3xl font-bold text-gray-800 mb-6">My Orders</h1>

                {orders.length === 0 ? (
                    <div className="bg-white p-6 rounded-lg shadow text-center text-gray-500">
                        You haven't placed any orders yet.
                    </div>
                ) : (
                    <div className="space-y-6">
                        {orders.map((order) => (
                            <div key={order.id} className="bg-white rounded-lg shadow-md overflow-hidden">
                                
                                {/* Order Header */}
                                <div className="bg-gray-50 p-4 border-b flex justify-between items-center">
                                    <div className="px-3">
                                        <span className="text-sm text-gray-500 block">Order #{order.id}</span>
                                        <span className="text-sm font-bold text-gray-700">
                                            {new Date(order.orderDate).toLocaleDateString()}
                                        </span>
                                    </div>
                                    <div className="text-right">
                                        <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                            order.status === "COMPLETED" ? "bg-green-100 text-green-800" : "bg-yellow-100 text-yellow-800"
                                        }`}>
                                            {order.status}
                                        </span>
                                        <span className="px-3 block font-bold text-gray-800 mt-1">${order.totalPrice}</span>
                                    </div>   
                                </div>

                                {/* Order Item */}
                                <div className="p-4">
                                    <h4 className="px-3 text-sm font-bold text-gray-500 mb-2">Items</h4>
                                    <ul className="space-y-2 px-3">
                                        {order.items.map((item, index) => (
                                            <li key={index} className="flex justify-between text-sm text-gray-700 border-b border-gray-100 pb-2 last:border-0">
                                                <span>{item.productName} <span className="text-gray-400">x {item.quantity}</span></span>
                                                <span>${item.totalLinePrice}</span>
                                            </li>
                                        ))}
                                    </ul>

                                    {/* Shipping Address Display */}
                                    <div className="px-3 mt-4 pt-4 border-t text-sm text-gray-500">
                                        <span className="font-bold">Shipped to: </span> {order.shippingAddress}
                                    </div>

                                </div>
                                
                            </div>
                        ))}
                    </div>    

                )}
            </div>
        </div>
    );
}