import Navbar from "../components/Navbar";

export default function AdminDashboard() {
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            <div className="max-w-7xl mx-auto p-8">
                <h1 className="text-3xl font-bold text-red-600 mb-6">Admin Dashboard</h1>
                <div className="bg-white p-6 rounded shadow">
                    <p>Welcome, Admin. Here you will manage your products.</p>
                </div>
            </div>
        </div>
    );
}