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
    });
};

export const forgotPasswordAPI = async(email) => {
    return await apiRequest("/auth/forgot-password", {
        method: "POST",
        body: JSON.stringify({ email })
    });
};

export const resetPasswordAPI = async(token, newPassword) => {
    return await apiRequest("/auth/reset-password", {
        method: "POST",
        body: JSON.stringify({ token, newPassword })
    });
};