import { apiRequest } from "./api";

export const loginAPI = async (email, password)  => {
    return await apiRequest("/auth/login", {
        method: "POST",
        body: JSON.stringify({ email, password })
    });
};

export const registerAPI = async (name, email, password) => {
    return await apiRequest("/auth/register", {
        method: "POST",
        body: JSON.stringify({ name, email, password })
    })
};