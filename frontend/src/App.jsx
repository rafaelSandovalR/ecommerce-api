import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./Login";
import Home from "./Home"

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<Home />} />
      <Route path="*" element={<Navigate to="/login" />} />
    </Routes>
  );
}