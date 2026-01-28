import { createContext, useState, useEffect, useContext } from "react";
import { jwtDecode } from "jwt-decode";

const AuthContext = createContext();

export const AuthProvider = ({ children}) => {
    const [user, setUser] = useState(null);

    const processToken = (token) => {
        try {
            const decoded = jwtDecode(token);
            if (decoded.exp * 1000 < Date.now()) {
                localStorage.removeItem("token");
                setUser(null);
                return;
            }

            // console.log("Decoded Token:", decoded);

            setUser({
                token,
                email: decoded.sub,
                role: decoded.roles || decoded.role || decoded.authorites || "ROLE_USER"
            });

        } catch (error) {
            console.error("Invalid token", error);
            localStorage.removeItem("token");
            setUser(null);
        }
    };

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (token) processToken(token);
    }, []);

    const login = (token) => {
        localStorage.setItem("token", token);
        processToken(token);
    }

    const logout = () => {
        localStorage.removeItem("token");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);