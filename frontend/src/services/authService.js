import { apiRequest } from "./api";

export const loginAPI = async (email, password)  => {
    return await apiRequest("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password })
    });
};