import Navbar from "./Navbar";

export default function Home() {
    return (
        <div className="min-h-screen bg-gray-100">
            <Navbar />
            
            <div className="p-10">
                <h1 className="text-4xl font-bold text-green-600">Welcome to the Store!</h1>
                <p className="mt-4 text-gray-600">You have successfully logged in.</p>
            </div>
        </div>
    )
}