import { apiRequest } from "./api";

export const createPaymentIntentAPI = async () => {
    return await apiRequest("/payment/create-payment-intent", { method: "POST"});
};