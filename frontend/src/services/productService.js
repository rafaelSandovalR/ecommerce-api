import { apiRequest } from "./api";

export const fetchAllProductsAPI = async (query = "") => {

    const endpoint = query
        ? `/products?query=${encodeURIComponent(query)}`
        : "/products";

    return await apiRequest(endpoint);
};