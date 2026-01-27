import { useEffect, useState } from "react";
import { addToCartAPI } from "../services/cartService";
import { fetchAllProductsAPI, fetchCategoriesAPI } from "../services/productService";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import Navbar from "../components/Navbar";


export default function Home() {
  const [products, setProducts] = useState([]); // Holds the list of products
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true); // Tracks if we are still waiting
  const [error, setError] = useState(null);
  const [addingId, setAddingId] = useState(null);

  // URL Params
  const [searchParams] = useSearchParams(); // Get the search params from the URL
  const searchQuery = searchParams.get("q") || ""; // Get '?q=...' or empty string

  // Filter State
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const data = await fetchCategoriesAPI();
        setCategories(data);
      } catch (err) {
        console.error("Failed to load categories", err);
      }
    };
    loadCategories();
  }, []);

  // Fetch Products whenever filters change
  useEffect(() => {
    loadProducts();
  }, [searchQuery, selectedCategory, minPrice, maxPrice]); // Whenever the URL changes, run this function again


  const loadProducts = async () => {
    setLoading(true);
    try {
      const data = await fetchAllProductsAPI({
        keyword: searchQuery,
        categoryId: selectedCategory,
        minPrice: minPrice,
        maxPrice: maxPrice
      });
      // Handle Spring Page vs List
      setProducts(data.content || data || []); // Save the data to our state
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false); // Stop the loading circle
    }
  };


  const handleAddToCart = async (productId) => {
    if (!user) {
      alert("Please login to add items to your cart.");
      navigate("/login");
      return;
    }

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

  if (error) return <div className="text-center mt-20 text-red-500">Error: {error}</div>;

  return (
    <div className="min-h-screen bg-gray-100">
      <Navbar />

      <div className="max-w-6xl mx-auto p-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">
          {searchQuery ? `Results for "${searchQuery}"` : "Featured Products"}
        </h1>

        {/* LAYOUT CONTAINER: Sidebar + Grid */}
        <div className="flex flex-col md:flex-row gap-8">

          {/* LEFT SIDEBAR */}
          <aside className="w-full md:w-1/4 bg-white p-4 rounded-lg shadow h-fit">
            <h3 className="font-bold text-lg mb-4 border-b pb-2">Filters</h3>

            {/* Category Filter */}
            <div className="mb-6">
              <h4 className="font-semibold mb-2 text-gray-700">Category</h4>
              <div className="flex flex-col gap-2 max-h-60 overflow-y-auto">
                <label className="flex items-center gap-2 cursor-pointer hover:text-blue-600">
                  <input
                    type="radio"
                    name="category"
                    checked={selectedCategory === null}
                    onChange={() => setSelectedCategory(null)}
                    className="accent-blue-600"
                  />
                  <span>All Categories</span>
                </label>

                {categories.map((cat) => (
                  <label key={cat.id} className="flex items-center gap-2 cursor-pointer hover:text-blue-600">
                    <input
                      type="radio"
                      name="category"
                      checked={selectedCategory === cat.id}
                      onChange={() => setSelectedCategory(cat.id)}
                      className="accent-blue-600"
                    />
                    <span>{cat.name}</span>
                  </label>
                ))}
              </div>
            </div>

            <div>
              <h4 className="font-semibold mb-2 text-gray-700">Price Range</h4>
              <div className="flex gap-2">
                <input
                  type="number"
                  placeholder="Min"
                  className="w-1/2 p-2 border rounded focus:outline-blue-500"
                  value={minPrice}
                  onChange={(e) => setMinPrice(e.target.value)}
                />
                <input
                  type="number"
                  placeholder="Max"
                  className="w-1/2 p-2 border rounded focus:outline-blue-500"
                  value={maxPrice}
                  onChange={(e) => setMaxPrice(e.target.value)}
                />
              </div>
            </div>
          </aside>

          {/* RIGHT CONTENT: Product Grid */}
          <main className="w-full md:w-3/4">
            <div className={`grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 transition-opacity duration-300 ${loading ? "opacity-50" : "opacity-100"}`}>

              {/* The Loop (.map) */}
              {products.map((product) => (
                <div key={product.id} className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">

                  {/* Product Image */}
                  <div className="h-48 bg-gray-200 overflow-hidden group">
                    {product.imageUrl ? (
                      <img
                        src={product.imageUrl}
                        alt={product.name}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-300"
                      />
                    ) : (
                      <div className="flex items-center justify-center h-full text-gray-500">
                        No Image
                      </div>
                    )}
                  </div>

                  <div className="p-4">
                    <h2 className="text-xl font-bold text-gray-800">{product.name}</h2>
                    <p className="text-gray-600 mt-2 text-sm">{product.description}</p>
                    <div className="mt-4 flex justify-between items-center">
                      <span className="text-blue-600 font-bold text-lg">${product.price}</span>
                      <button
                        onClick={() => handleAddToCart(product.id)}
                        disabled={addingId === product.id} // Disable if currently adding this
                        className={`px-4 py-2 rounded-md transition text-white ${addingId === product.id ? "bg-gray-400 cursor-not-allowed" : "bg-blue-600 hover:bg-blue-700"
                          }`}
                      >
                        {addingId === product.id ? "Adding..." : "Add to Cart"}
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* "No Results" Message */}
            {!loading && products.length == 0 && (
              <div className="flex flex-col items-center justify-center mt-10 text-gray-500">
                <p className="text-lg">No products found matching your filters.</p>
                <button
                onClick={() => {
                  setSelectedCategory(null);
                  setMinPrice("");
                  setMaxPrice("");
                }}
                className="mt-4 text-blue-600 hover:underline"
                >
                  Clear all filters
                </button>
              </div>
            )}
          </main>
        </div>
      </div>
    </div>
  );
}