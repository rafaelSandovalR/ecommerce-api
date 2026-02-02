import { useState } from "react";
import AdminProductList from "../components/AdminProductList";
import Navbar from "../components/Navbar";
import { addProductAPI, updateProductAPI } from "../services/productService";
import ProductFormModal from "../components/ProductFormModal";

export default function AdminDashboard() {
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [refreshKey, setRefreshKey] = useState(0);

    const handleAddClick = () => {
        setEditingProduct(null); // Clear previous data
        setIsModalOpen(true);
    };

    const handleEditClick = (product) => {
        setEditingProduct(product);
        setIsModalOpen(true);
    };

    const handleFormSubmit = async (productData) => {
        try {
            if (editingProduct) {
                await updateProductAPI(editingProduct.id, productData);
                alert("Product Updated!");
            } else {
                await addProductAPI(productData);
                alert("Product Created!");
            }
            setIsModalOpen(false);
            setRefreshKey(prev => prev + 1);
        } catch (err) {
            alert("Operation failed: " + err.message);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-7xl mx-auto p-8">
                <div className="flex justify-between items-center mb-6">
                    <h1 className="text-3xl font-bold text-gray-800">Product Manager</h1>
                    <button
                        onClick={handleAddClick}
                        className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
                    >
                        + Add New Product
                    </button>
                </div>

                <AdminProductList 
                    key={refreshKey}
                    onEdit={handleEditClick}
                />

                {isModalOpen && (
                    <ProductFormModal
                        initialData={editingProduct}
                        onClose={() => setIsModalOpen(false)}
                        onSubmit={handleFormSubmit}
                    />
                )}
            </div>
        </div>
    );
}