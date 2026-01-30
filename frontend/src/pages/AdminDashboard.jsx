import { useState } from "react";
import AdminProductList from "../components/AdminProductList";
import Navbar from "../components/Navbar";
import { addProductAPI } from "../services/productService";
import ProductFormModal from "../components/ProductFormModal";

export default function AdminDashboard() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);

    const handleCreateProduct = async (productData) => {
        try {
            await addProductAPI(productData);
            alert("Product Created!");
            setIsModalOpen(false); // Close modal
            setRefreshKey(prev => prev + 1); // Trigger list refresh
        } catch (err) {
            alert("Failed to create: " + err.message);
        }
    }
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-7xl mx-auto p-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-800">Product Manager</h1>
                    <button
                        onClick={() => setIsModalOpen(true)}
                        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                    >
                        + Add New Product
                    </button>
                </div>

                <AdminProductList key={refreshKey} />

                {isModalOpen && (
                    <ProductFormModal
                        onClose={() => setIsModalOpen(false)}
                        onSubmit={handleCreateProduct}
                    />
                )}
            </div>
        </div>
    );
}