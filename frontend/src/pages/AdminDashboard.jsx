import AdminProductList from "../components/AdminProductList";
import Navbar from "../components/Navbar";

export default function AdminDashboard() {
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-7xl mx-auto p-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-800">Product Manager</h1>
                    <button className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
                        + Add New Product
                    </button>
                </div>

                <AdminProductList />
            </div>
        </div>
    );
}