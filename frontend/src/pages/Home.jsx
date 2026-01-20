import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { addToCartAPI } from "../services/cartService";

export default function Home() {
  const [products, setProducts] = useState([]); // Holds the list of products
  const [loading, setLoading] = useState(true); // Tracks if we are still waiting
  const [error, setError] = useState(null);
  const [addingId, setAddingId] = useState(null); 

  // The "Effect" Hook: Runs once when the page loads
  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/api/products", {
          headers: {
            "Authorization": `Bearer ${token}`
          }
        });

        if (!response.ok) {
          throw new Error("Failed to fetch products");
        }

        const data = await response.json();
        // Handle Spring Page vs List
        setProducts(data.content || data || []); // Save the data to our state
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false); // Stop the loading circle
      }
    };

    fetchProducts();
  }, []); // The empty [] means: "Only run this ONE time on startup"

  const handleAddToCart = async (productId) => {
    setAddingId(productId); // Show loading state on the specific button

    try {
      await addToCartAPI(productId, 1);
      alert("Item added to cart!");
    } catch (err) {
      console.error(err);
      alert("Error adding item: " + err.message);
    } finally {
      setAddingId(null);
    }
  };


  if (loading) return <div className="text-center mt-20">Loading products...</div>;
  if (error) return <div className="text-center mt-20 text-red-500">Error: {error}</div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />

      <div className="max-w-6xl mx-auto p-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">Featured Products</h1>
        
        {/* The Grid Layout */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          {/* The Loop (.map) */}
          {products.map((product) => (
            <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
              
              {/* Product Image (Placeholder for now) */}
              <div className="h-48 bg-gray-200 flex items-center justify-center text-gray-500">
                 [Image Placeholder]
              </div>

              <div className="p-4">
                <h2 className="text-xl font-bold text-gray-800">{product.name}</h2>
                <p className="text-gray-600 mt-2 text-sm">{product.description}</p>
                <div className="mt-4 flex justify-between items-center">
                  <span className="text-blue-600 font-bold text-lg">${product.price}</span>
                  <button 
                    onClick={() => handleAddToCart(product.id)}
                    disabled={addingId === product.id} // Disable if currently adding this
                    className={`px-4 py-2 rounded-md transition text-white ${
                      addingId === product.id ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
                    }`}
                  >
                    {addingId === product.id ? "Adding..." : "Add to Cart"}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}