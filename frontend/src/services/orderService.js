import { apiRequest } from "./api";

export const placeOrderAPI = async (shippingAddress) => {
    return await apiRequest("/orders/place", {
        method: "POST",
        body: JSON.stringify({ shippingAddress })
    });
};

export const fetchOrdersAPI = async () => {
    return await apiRequest("/orders");
};