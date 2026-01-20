const API_URL = "http://localhost:8080/api/carts";

const getToken = () => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("No token found");
    return token;  
};

const getAuthHeaders = () => {
    const token = getToken();
    return {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
    };
};

export const fetchCartAPI = async () => {
    const token = getToken();

    const response = await fetch(API_URL, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Failed to fetch cart");
    return await response.json();
};

export const addToCartAPI = async (productId, quantity = 1) => {
    const response = await fetch(`${API_URL}/add`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({
            productId: productId,
            quantity: quantity
        })
    });

    if (!response.ok) throw new Error("Failed to add item");
    return await response.json();
};

export const removeFromCartAPI = async (itemId) => {
    const token = getToken();

    const response = await fetch(`${API_URL}/remove/${itemId}`, {
        method: "DELETE",
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Failed to remove item");
    return await response.json();
};