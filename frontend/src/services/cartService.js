import { apiRequest } from "./api";


export const fetchCartAPI = async () => {
    return await apiRequest("/carts");
};

export const addToCartAPI = async (productId, quantity = 1) => {
    return await apiRequest("/carts/add", {
        method: "POST",
        body: JSON.stringify({ productId, quantity })
    });
};

export const removeFromCartAPI = async (itemId) => {
    return await apiRequest(`/carts/remove/${itemId}`, { method: "DELETE" });
};