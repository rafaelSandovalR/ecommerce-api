import { apiRequest } from "./api";

export const fetchAllProductsAPI = async () => {
    return await apiRequest("/products");
};