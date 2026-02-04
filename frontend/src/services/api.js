const BASE_URL = "http://localhost:8080/api";

export const apiRequest = async (endpoint, options = {}) => {
    const token = localStorage.getItem("token");

    const headers = {
        "Content-Type": "application/json",
        ...options.headers, // Allow overriding headers if needed
    };

    // If sending a file (FormData), let the browser set the Content-Type
    if (options.body instanceof FormData) {
        delete headers["Content-Type"];
    }

    if (token) headers["Authorization"] = `Bearer ${token}`;

    const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        window.location.href = "/login";
        throw new Error("Session expired or unauthorized");
    }
    
    if (!response.ok) {
        const errorBody = await response.json().catch(() => ({}));
        throw new Error(errorBody.message || "API Request Failed");
    }

    if (response.status === 204) return null;

    return await response.json();
};