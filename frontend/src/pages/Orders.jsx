import { useEffect, useState } from "react";
import { fetchOrdersAPI } from "../services/orderService";
import { getStatusColor } from "../utils/orderStatus";
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
                                        <span className={`mr-2 px-3 py-1 rounded-full text-xs font-bold ${getStatusColor(order.status)}`}>
                                            {order.status}
                                        </span>
                                        <span className="px-3 block font-bold text-gray-800 mt-1">${Number(order.totalPrice).toFixed(2)}</span>
                                    </div>   
                                </div>
                                
                                <div className="py-4 px-10">
                                    {/* Grid Header */}
                                    <div className="grid grid-cols-10 py-2 border-b text-gray-500 font-bold text-sm">
                                        <div className="col-span-4">Items</div>
                                        <div className="col-span-2 text-right">Qty</div>
                                        <div className="col-span-2 ml-8">Price/Unit</div>
                                        <div className="col-span-2 text-right">Subtotal</div>
                                    </div>

                                    <div className="flex flex-col">
                                        {order.items.map((item) => (
                                            <div 
                                                key={item.productId} 
                                                className="grid grid-cols-10 py-4 border-b border-gray-100 items-center text-sm text-gray-700 last:border-b-0"
                                            >
                                                <span className="col-span-4">{item.productName}</span>
                                                <span className="col-span-2 text-right">{item.quantity}</span>
                                                <span className="col-span-2 ml-8">${Number(item.pricePerUnit).toFixed(2)}</span>
                                                <span className="col-span-2 text-right">${Number(item.totalLinePrice).toFixed(2)}</span>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                                
                                {/* Shipping Address Display */}
                                <div className="p-7 text-sm text-gray-500 border-t">
                                    <span className="font-bold">Shipped to: </span> {order.shippingAddress}
                                </div>
                            </div>
                        ))}
                    </div>    

                )}
            </div>
        </div>
    );
}