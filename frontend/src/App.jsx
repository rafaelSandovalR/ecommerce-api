import { Routes, Route, Navigate } from "react-router-dom";
import Register from "./pages/Register";
import Login from "./pages/Login";
import Home from "./pages/Home"
import Cart from "./pages/Cart";
import Checkout from "./pages/Checkout";
import Orders from "./pages/Orders";
import OrderSuccess from "./pages/OrderSuccess";
import ProtectedRoute from "./components/ProtectedRoute";


export default function App() {
  return (
    <Routes>
      {/* Unlocked */}
      <Route path="/register" element={<Register />}/>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Home />}/>

      {/* Locked */}
      <Route path="/cart" element={<ProtectedRoute><Cart /></ProtectedRoute>} />
      <Route path="/checkout" element={<ProtectedRoute><Checkout /></ProtectedRoute>} />
      <Route path="/order-success" element={<ProtectedRoute><OrderSuccess /></ProtectedRoute>} />
      <Route path="/orders" element={<ProtectedRoute><Orders /></ProtectedRoute>} />

      {/* Default */}
      <Route path="*" element={<Navigate to="/login" />} />
    </Routes>
  );
}