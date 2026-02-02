import { useEffect, useState } from "react";
import { deleteProductAPI, fetchAllProductsAPI } from "../services/productService";

export default function AdminProductList({ onEdit }) {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);

    useEffect(() => {
        loadProducts();
    }, []);

    const loadProducts = async () => {
        try {
            const data = await fetchAllProductsAPI({});
            setProducts(data.content || data || []);
        } catch (err) {
            console.error("Failed to load products", err);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this product?")) return;

        try {
            await deleteProductAPI(id);

            setProducts(products.filter(p => p.id !== id));
            alert("Product deleted!");
        } catch (err) {
            alert("Error deleting product: " + err.message);
        }

    };

    return (
        <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                    <tr>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Product</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Price</th>
                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Stock</th>
                        <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                    </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                    {products.map((product) => (
                        <tr key={product.id}>
                            <td className="px-6 py-4 whitespace-nowrap">
                                <div className="flex items-center">
                                    <div className="h-10 w-10 flex-shrink-0">
                                        <img className="h-10 w-10 rounded-full object-cover" src={product.imageUrl || "https://via.placeholder.com/40"} alt="" />
                                    </div>
                                    <div className="ml-4">
                                        <div className="text-sm font-medium text-gray-900">{product.name}</div>
                                        <div className="text-sm text-gray-500">{product.categoryName || "Uncategorized"}</div>
                                    </div>
                                </div>
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                ${product.price}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                {product.stockQuantity}
                            </td>
                            <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                <button 
                                    onClick={() => onEdit(product)}
                                    className="text-indigo-600 hover:text-indigo-900 mr-4"
                                >
                                    Edit</button>
                                <button 
                                    onClick={() => handleDelete(product.id)}
                                    className="text-red-600 hover:text-red-900"
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>

            </table>
        </div>
    );
}