import { apiRequest } from "./api";

export const fetchAllProductsAPI = async ({ keyword, categoryId, minPrice, maxPrice } = {}) => {

    // Create a URLSearchParams object to build the query string automatically
    const params = new URLSearchParams();

    if (keyword) params.append("keyword", keyword);
    if (categoryId) params.append("categoryId", categoryId);
    if (minPrice) params.append("minPrice", minPrice);
    if (maxPrice) params.append("maxPrice", maxPrice);

    return await apiRequest(`/products?${params.toString()}`);
};

export const fetchCategoriesAPI = async () => {
    return await apiRequest("/categories");
};