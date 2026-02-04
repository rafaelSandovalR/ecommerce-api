import { useState, useEffect } from "react";
import { fetchCategoriesAPI } from "../services/productService";
import { uploadFileAPI } from "../services/fileUploadService";

export default function ProductFormModal({ onClose, onSubmit, initialData }) {
    const [categories, setCategories] = useState([]);
    const [uploading, setUploading] = useState(false);
    const [formData, setFormData] = useState({
        name: "",
        description: "",
        price: "",
        stockQuantity: "",
        categoryId: "",
        imageUrl: ""
    });

    useEffect(() => {
        fetchCategoriesAPI().then(setCategories);
        if (initialData) {
            setFormData({
                name: initialData.name,
                description: initialData.description || "",
                price: initialData.price,
                stockQuantity: initialData.stockQuantity,
                categoryId: initialData.categoryId,
                imageUrl: initialData.imageUrl || ""
            });
        }
    }, [initialData]);

    const handleFileChange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setUploading(true);
        try {
            const secureUrl = await uploadFileAPI(file);    // Send file to backend -> Cloudinary
            setFormData(prev => ({ ...prev, imageUrl: secureUrl})); // Put the returned URL into the form data
        } catch (error) {
            alert("Failed to upload image: " + error.message);
        } finally {
            setUploading(false);
        }
    };

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (parseInt(formData.stockQuantity) < 0) {
            alert("Stock cannot be negative.");
            return;
        }

        if (parseFloat(formData.price) <= 0) {
            alert("Price must be greater than zero.");
            return;            
        }

        const payload = {
            ...formData,
            price: parseFloat(formData.price),
            stockQuantity: parseInt(formData.stockQuantity),
            categoryId: parseInt(formData.categoryId)
        };
        
        onSubmit(payload);
    };

    return (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex justify-center items-center z-50">
            <div className="bg-white p-6 rounded shadow-lg w-96">
                <h2 className="text-xl font-bold mb-4">
                    {initialData ? "Edit Product" : " Add New Product"}
                </h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    {/* Name */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Name</label>
                        <input name="name" value={formData.name} onChange={handleChange} className="w-full border p-2 rounded" required />
                    </div>

                    {/* Description */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Description</label>
                        <input name="description" value={formData.description} onChange={handleChange} className="w-full border p-2 rounded" />
                    </div>

                    {/* Price & Stock Row */}
                    <div className="flex gap-2">
                        <div className="w-1/2">
                            <label className="block text-sm font-medium text-gray-700">Price ($)</label>
                            <input 
                                name="price" 
                                type="number" 
                                step="0.01"
                                min="0"
                                value={formData.price} 
                                onChange={handleChange} 
                                className="w-full border p-2 rounded" 
                                required
                            />
                        </div>
                        <div className="w-1/2">
                            <label className="block text-sm font-medium text-gray-700">Stock</label>
                            <input 
                                name="stockQuantity" 
                                type="number"
                                min="0"
                                value={formData.stockQuantity} 
                                onChange={handleChange} 
                                className="w-full border p-2 rounded" 
                                required
                            />
                        </div>                        
                    </div>

                    {/* Category Dropdown */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Category</label>
                        <select name="categoryId" value={formData.categoryId} onChange={handleChange} className="w-full border p-2 rounded" required>
                            <option value="">Select Category</option>
                            {categories.map(c => (
                                <option key={c.id} value={c.id}>{c.name}</option>
                            ))}
                        </select>
                    </div>

                    {/* Image Upload */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700">Product Image</label>
                        <div className="flex items-center gap-2">
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleFileChange}
                                className="w-full border p-2 rounded"
                            />
                            {/* Loading Indicator */}
                            {uploading && <span className="text-blue-600 text-sm">Uploading...</span>}
                        </div>
                        {formData.imageUrl && (
                            <div className="mt-2">
                                <p className="text-xs text-gray-500 mb-1">Preview:</p>
                                <img
                                    src={formData.imageUrl}
                                    alt="Preview"
                                    className="w-20 h-20 object-cover rounded border"
                                />
                                {/* Hidden input to store the actual string URL for submission */}
                                <input type="hidden" name="imageUrl" value={formData.imageUrl} />
                            </div>
                        )}
                    </div>

                    {/* Buttons */}
                    <div className="flex justify-end gap-2 mt-4">
                        <button type="button" onClick={onClose} className="px-4 py-2 text-gray-600 hover:text-gray-800">Cancel</button>
                        <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
                            {initialData ? "Update" : "Save"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};