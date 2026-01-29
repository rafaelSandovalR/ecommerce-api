import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ roleRequired }) {
    const { user, loading } = useAuth();

    if (loading) return <div>Loading...</div>

    // Not logged in
    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // Logged in, but wrong role
    if (roleRequired && user.role !== roleRequired) {
        return <Navigate to="/" replace/>
    }

    // Allowed. Render the child route
    return <Outlet />;
}