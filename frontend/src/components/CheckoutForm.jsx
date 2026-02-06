import { useEffect, useState } from "react";
import { PaymentElement, useStripe, useElements } from "@stripe/react-stripe-js";
import { apiRequest } from "../services/api";

export default function CheckoutForm({ onSuccess, address }) {
    const stripe = useStripe();
    const elements = useElements();

    const[message, setMessage] = useState(null);
    const[loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!address || address.length < 5) {
            setMessage("Please enter a valid shipping address first.");
            return;
        }

        if (!stripe || !elements) return; // Stripe.js hasn't yet loaded.
        
        setLoading(true);
        
        // Confirm payment with Stripe
        const { error, paymentIntent } = await stripe.confirmPayment({
            elements,
            confirmParams: {
                return_url: window.location.origin + "/order-success",
            },
            redirect: "if_required",
        });

        if (error) {
            setMessage(error.message);
            setLoading(false);
        } else if (paymentIntent && paymentIntent.status === "succeeded") {
            // Payment Success, now place the order in backend
            onSuccess(paymentIntent.id);
        } else {
            setMessage("Unexpected state.");
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-6">
            <PaymentElement />
            {message && <div className="text-red-500 text-sm">{message}</div>}

            <button
                disabled={loading || !stripe || !elements}
                className="w-full bg-blue-600 text-white py-3 rounded hover:bg-blue-700 disabled:opacity-50 font-bold"
            >
                {loading ? "Processing..." : "Pay Now"}
            </button>
        </form>
    )
}