import { createContext, useContext, useState, useEffect, useCallback} from "react";
import { addToCartAPI, clearCartAPI, fetchCartAPI, removeFromCartAPI, updateCartItemAPI} from "../services/cartService";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const refreshCart =  useCallback(async () => {
        try{
            const data = await fetchCartAPI();
            setCart(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }, []);
    
        useEffect(() => {
        refreshCart();
    }, [refreshCart]);

    const addToCart = async (productId, quantity = 1) => {
        try {
            await addToCartAPI(productId, quantity);
            await refreshCart();
        } catch (error) {
            throw error; // For button handling in product page
        }
    };

    const removeFromCart = async (itemId) => {
        //if (!confirm("Are you sure you want to remove this item")) return;
        
        try {
            await removeFromCartAPI(itemId);
            await refreshCart();
        } catch (error) {
            alert("Failed to remove from cart" + error.message);
        }
    };

    const updateQuantity = async (itemId, quantity) => {
        if (quantity < 1) return;
        
        try {
            await updateCartItemAPI(itemId, quantity);
            await refreshCart();
        } catch (error) {
            alert("Failed to update: " + error.message);
        }
    };

    const clearCart = async () => {
        try {
            await clearCartAPI();
            await refreshCart();
        } catch (error) {
            alert("Failed to clear cart: " + error.message);
        }
    };

    const totalItems = cart?.items?.reduce((sum, item) => sum + item.quantity, 0) || 0;

    return (
        <CartContext.Provider value={{
            cart, loading, error, totalItems,
            refreshCart, addToCart, removeFromCart, updateQuantity, clearCart
        }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);