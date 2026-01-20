const API_URL = "http://localhost:8080/api/products";

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

export const fetchAllProductsAPI = async () => {
    const token = getToken();
    const response = await fetch(API_URL, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Failed to fetch products");
    return await response.json();
};