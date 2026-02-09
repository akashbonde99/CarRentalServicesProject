import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import { FaCreditCard, FaLock, FaCheckCircle } from 'react-icons/fa';

const Checkout = () => {
    const { bookingId } = useParams();
    const navigate = useNavigate();

    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(true);
    const [processing, setProcessing] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState('');

    // ✅ ADD: get logged-in user
    const user = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        const fetchBooking = async () => {
            try {
                const response = await api.get(`/bookings/${bookingId}`);
                if (response.data.success) {
                    setBooking(response.data.data);
                }
            } catch {
                setError("Could not retrieve booking details.");
            } finally {
                setLoading(false);
            }
        };
        fetchBooking();
    }, [bookingId]);

    const loadRazorpay = () => {
        return new Promise((resolve) => {
            if (window.Razorpay) {
                resolve(true);
                return;
            }
            const script = document.createElement("script");
            script.src = "https://checkout.razorpay.com/v1/checkout.js";
            script.onload = () => resolve(true);
            script.onerror = () => resolve(false);
            document.body.appendChild(script);
        });
    };

    const handlePayment = async () => {
        setProcessing(true);
        setError('');

        const sdkLoaded = await loadRazorpay();
        if (!sdkLoaded) {
            setError("Razorpay SDK failed to load.");
            setProcessing(false);
            return;
        }

        try {
            const orderResponse = await api.post("/payments/create-order", {
                amount: Number(booking.totalAmount),
                currency: "INR"
            });

            const order = orderResponse.data;

            const options = {
                key: "rzp_test_S9HcVYCQnsXY5t",
                amount: order.amount,
                currency: order.currency,
                name: "Car Booking Website",
                description: "Car Booking Payment",
                order_id: order.id,

                //  FIX: Razorpay prefill
                prefill: {
                    name: user?.name || "",
                    email: user?.email || "",
                    contact: user?.phoneNumber || ""
                },

                handler: async function (response) {
                    try {
                        const confirmResponse = await api.post("/payments", {
                            bookingId: booking.bookingId,
                            amount: booking.totalAmount,
                            paymentMode: "ONLINE",
                            razorpayPaymentId: response.razorpay_payment_id,
                            razorpayOrderId: response.razorpay_order_id,
                            razorpaySignature: response.razorpay_signature,
                            paymentDate: new Date().toISOString().split("T")[0]
                        });

                        if (confirmResponse.data.success) {
                            setSuccess(true);
                            setTimeout(() => navigate("/my-bookings"), 3000);
                        } else {
                            alert("Server failed to save payment record: " + confirmResponse.data.message);
                            setError("Payment verified but record not saved. Please contact support.");
                        }
                    } catch (err) {
                        console.error("Payment confirmation error:", err);
                        alert("Error communicating with backend: " + err.message);
                        setError("Critical error saving payment. Please check your dashboard.");
                    }
                },

                theme: {
                    color: "#4f46e5"
                }
            };

            const razorpay = new window.Razorpay(options);
            razorpay.open();

        } catch (err) {
            console.error(err);
            setError("Payment failed. Please try again.");
        } finally {
            setProcessing(false);
        }
    };

    if (loading) return <div className="p-20 text-center">Finalizing invoice...</div>;
    if (!booking) return <div className="p-20 text-center text-red-600 font-bold">Booking not found.</div>;

    if (success) {
        return (
            <div className="flex flex-col items-center justify-center p-20 bg-gray-50 min-h-[70vh]">
                <FaCheckCircle className="text-green-500 mb-6" size={80} />
                <h2 className="text-4xl font-bold text-gray-800 mb-2">Payment Successful!</h2>
                <p className="text-gray-500 mb-8">Your reservation has been confirmed.</p>
                <p className="text-gray-400 text-sm italic">Redirecting...</p>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-6 py-12 max-w-4xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Complete Your Booking</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
                <div className="bg-white p-8 rounded-lg border shadow-sm">
                    <h3 className="text-xl font-bold mb-6 border-b pb-4">Reservation Summary</h3>
                    <div className="space-y-4 text-sm">
                        <div className="flex justify-between">
                            <span className="text-gray-500">Vehicle</span>
                            <span className="font-bold">
                                {booking.car?.brand} {booking.car?.model}
                            </span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-gray-500">Pickup</span>
                            <span className="font-bold">{booking.pickupDate}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-gray-500">Return</span>
                            <span className="font-bold">{booking.dropDate}</span>
                        </div>
                        <div className="pt-4 border-t flex justify-between text-lg">
                            <span className="font-bold">Total</span>
                            <span className="font-black text-indigo-600">
                                ₹{booking.totalAmount}
                            </span>
                        </div>
                    </div>
                </div>

                <div className="bg-white p-8 rounded-lg border shadow-sm">
                    <h3 className="text-xl font-bold mb-6 flex items-center">
                        <FaCreditCard className="mr-3 text-indigo-600" />
                        Secure Payment
                    </h3>

                    {error && (
                        <div className="bg-red-50 text-red-600 p-3 rounded text-xs mb-6 font-bold">
                            {error}
                        </div>
                    )}

                    <button
                        onClick={handlePayment}
                        disabled={processing}
                        className={`w-full bg-indigo-600 text-white py-4 rounded-md font-bold transition-all shadow-lg
                        ${processing ? 'opacity-50 cursor-not-allowed' : 'hover:bg-indigo-700'}`}
                    >
                        {processing ? "Redirecting to Razorpay..." : `Pay ₹${booking.totalAmount}`}
                    </button>

                    <p className="text-[10px] text-gray-400 text-center mt-4 flex items-center justify-center italic">
                        <FaLock className="mr-2" /> Powered by Razorpay
                    </p>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
