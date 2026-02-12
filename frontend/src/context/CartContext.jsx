import { createContext, useContext, useState, useEffect} from "react";
import { addToCartAPI, clearCartAPI, fetchCartAPI, removeFromCartAPI, updateCartItemAPI} from "../services/cartService";

const CartContext = createContext();

export const CartProvider = ({ children }) => {
    const [cart, setCart] = useState([]);
    const [totalPrice, setTotalPrice] = useState(0);

    useEffect(() => {
        refreshCart();
    }, []);

    const refreshCart = async () => {
        try {
            const data = await fetchCartAPI();
            setCart(data.items || []);
            setTotalPrice(data.totalPrice || 0);
        } catch (error) {
            console.error("Failed to load cart: ", error);
        }
    };

    const addToCart = async (productId, quantity) => {
        try {
            await addToCartAPI(productId, quantity);
            await refreshCart();
        } catch (error) {
            console.error("Failed to add item: ", error);
            throw error; // For button handling in product page
        }
    };

    const removeFromCart = async (itemId) => {
        if (!confirm("Are you sure you want to remove this item")) return;
        
        try {
            await removeFromCartAPI(itemId);
            await refreshCart();
        } catch (error) {
            console.error("Failed to remove item: ", error);
            // TODO: Add a setError state to show a toast message;
        }
    };

    const updateQuantity = async (itemId, quantity) => {
        if (quantity < 1) return;
        
        try {
            await updateCartItemAPI(itemId, quantity);
            await refreshCart();
        } catch (error) {
            console.error("Failed to update quantity: ", error);
        }
    };

    const clearCart = async () => {
        try {
            await clearCartAPI();
            setCart([]);
            setTotalPrice(0);
        } catch (error) {
            console.error("Failed to clear cart: ", error);
        }
    };

    const totalItems = cart.reduce((sum, item) => sum + item.quantity, 0);

    return (
        <CartContext.Provider value={{
            cart,
            totalPrice,
            totalItems,
            addToCart,
            removeFromCart,
            updateQuantity,
            clearCart
        }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => useContext(CartContext);