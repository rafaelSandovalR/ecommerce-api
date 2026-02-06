import { useEffect, useState } from "react";
import { getAllOrdersAPI, updateOrderStatusAPI } from "../services/orderService";
import { getStatusColor } from "../utils/orderStatus";

export default function AdminOrderList() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        loadOrders();
    }, [page]);

    const loadOrders = async () => {
        setLoading(true);
        try {
            const data = await getAllOrdersAPI(page);
            setOrders(data.content);
            setTotalPages(data.totalPages);
        } catch (error) {
            console.error("Failed to fetch orders", error);
        } finally {
            setLoading(false);
        }
    };

    const handleStatusChange = async (orderId, newStatus) => {
        try {
            await updateOrderStatusAPI(orderId, newStatus);
            alert("Order status updated!");
            loadOrders();
        } catch (error) {
            alert("Failed to update status")
        }
    };

    return (
        <div className="bg-white p-6 rounded shadow">
            {loading ? <p>Loading orders...</p> : (
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse border border-gray-200">
                        <thead>
                            <tr className="bg-gray-100">
                                <th className="border p-2">Order ID</th>
                                <th className="border p-2">Customer</th>
                                <th className="border p-2">Date</th>
                                <th className="border p-2">Total</th>
                                <th className="border p-2">Status</th>
                                <th className="border p-2">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {orders.map(order => (
                                <tr key={order.id} className="text-center border-t">
                                    <td className="p-2">#{order.id}</td>
                                    <td className="p-2 text-sm text-gray-600">{order.userEmail}</td>
                                    <td className="p-2 text-sm">{new Date(order.orderDate).toLocaleDateString()}</td>
                                    <td className="p-2 font-bold">${order.totalPrice.toFixed(2)}</td>
                                    <td className="p-2">
                                        <span className={`px-2 py-1 rounded text-xs font-bold ${getStatusColor(order.status)}`}>
                                            {order.status}
                                        </span>
                                    </td>
                                    <td className="p-2">
                                        <select
                                            className="border p-1 rounded text-sm"
                                            value={order.status}
                                            onChange={(e) => handleStatusChange(order.id, e.target.value)}
                                        >
                                            <option value="PENDING">Pending</option>
                                            <option value="PAID">Paid</option>
                                            <option value="SHIPPED">Shipped</option>
                                            <option value="DELIVERED">Delivered</option>
                                            <option value="CANCELLED">Cancelled</option>
                                        </select>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <div className="mt-4 flex justify-center items-center">
                        <button
                            disabled={page === 0}
                            onClick={() => setPage(p => p - 1)}
                            className="w-24 py-2 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Previous
                        </button>
                        <span className="mx-4">Page {page + 1} of {totalPages}</span>
                        <button
                            disabled={page + 1 >= totalPages}
                            onClick={() => setPage(p => p + 1)}
                            className="w-24 py-2 bg-gray-200 rounded disabled:opacity-50"
                        >
                            Next
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}