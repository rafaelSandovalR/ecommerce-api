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

export const getAllOrdersAPI = async (page = 0, size = 10) => {
    return await apiRequest(`/admin/orders?page=${page}&size=${size}`);
};

export const updateOrderStatusAPI = async(orderId, status) => {
    return await apiRequest(`/admin/orders/${orderId}/status`, {
        method: "PUT",
        body: JSON.stringify({ status })
    })
};