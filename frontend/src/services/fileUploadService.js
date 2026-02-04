import { apiRequest } from "./api";

export const uploadFileAPI = async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    const data = await apiRequest("/upload", {
        method: "POST",
        body: formData
    });

    return data.url;
};