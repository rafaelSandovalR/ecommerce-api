const API_URL = "http://localhost:8080/api/orders";

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

export const placeOrderAPI = async (shippingAddress) => {
    const response = await fetch(`${API_URL}/place`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({
            shippingAddress: shippingAddress
        })
    });

    if (!response.ok) throw new Error("Failed to place order");
    return await response.json();
};

export const fetchOrdersAPI = async () => {
    const token = getToken();

    const response = await fetch(API_URL, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!response.ok) throw new Error("Failed to fetch orders");
    return await response.json();
};