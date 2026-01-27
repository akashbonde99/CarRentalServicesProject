import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { processPayment } from '../services/paymentService';
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

    const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');

    useEffect(() => {
        const fetchBooking = async () => {
            try {
                const response = await api.get(`/bookings/${bookingId}`);
                if (response.data.success) {
                    setBooking(response.data.data);
                }
            } catch (err) {
                setError("Could not retrieve booking details.");
            } finally {
                setLoading(false);
            }
        };
        fetchBooking();
    }, [bookingId]);

    const handlePayment = async (e) => {
        e.preventDefault();
        setProcessing(true);
        setError('');

        try {
            const data = await processPayment({
                bookingId: parseInt(bookingId),
                amount: booking.totalAmount,
                paymentMode: paymentMethod
            });

            if (data.success) {
                setSuccess(true);
                setTimeout(() => {
                    navigate('/my-bookings');
                }, 3000);
            } else {
                setError(data.message || "Payment processing failed.");
            }
        } catch (err) {
            setError("Error connecting to payment gateway.");
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
                <p className="text-gray-500 mb-8">Your reservation has been confirmed and paid. Happy driving!</p>
                <p className="text-gray-400 text-sm italic">Redirecting to your dashboard...</p>
            </div>
        );
    }

    return (
        <div className="container mx-auto px-6 py-12 max-w-4xl">
            <h2 className="text-3xl font-bold mb-8 text-center">Complete Your Booking</h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
                {/* Summary */}
                <div className="bg-white p-8 rounded-lg border border-gray-200 shadow-sm h-fit">
                    <h3 className="text-xl font-bold mb-6 border-b pb-4">Reservation Summary</h3>
                    <div className="space-y-4 text-sm">
                        <div className="flex justify-between">
                            <span className="text-gray-500">Vehicle</span>
                            <span className="font-bold">{booking.car?.brand} {booking.car?.model}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-gray-500">Pickup Date</span>
                            <span className="font-bold">{booking.pickupDate}</span>
                        </div>
                        <div className="flex justify-between">
                            <span className="text-gray-500">Return Date</span>
                            <span className="font-bold">{booking.dropDate}</span>
                        </div>
                        <div className="pt-4 border-t border-gray-100 flex justify-between items-center text-lg">
                            <span className="font-bold">Total Amount</span>
                            <span className="font-black text-indigo-600">₹{booking.totalAmount?.toLocaleString()}</span>
                        </div>
                    </div>
                </div>

                {/* Payment Form */}
                <div className="bg-white p-8 rounded-lg border border-gray-200 shadow-sm">
                    <h3 className="text-xl font-bold mb-6 flex items-center">
                        <FaCreditCard className="mr-3 text-indigo-600" /> Payment Details
                    </h3>

                    {error && <div className="bg-red-50 text-red-600 p-3 rounded text-xs mb-6 border border-red-100 font-bold">{error}</div>}

                    <form onSubmit={handlePayment} className="space-y-6">
                        <div>
                            <label className="block text-xs font-black uppercase tracking-widest text-gray-400 mb-3">Select Method</label>
                            <div className="grid grid-cols-2 gap-4">
                                <button
                                    type="button"
                                    onClick={() => setPaymentMethod('CREDIT_CARD')}
                                    className={`py-3 px-4 rounded-md border text-xs font-bold transition-all ${paymentMethod === 'CREDIT_CARD' ? 'bg-indigo-600 text-white border-indigo-600 shadow-md' : 'bg-gray-50 text-gray-500 border-gray-200 hover:bg-gray-100'}`}
                                >
                                    Credit Card
                                </button>
                                <button
                                    type="button"
                                    onClick={() => setPaymentMethod('UPI')}
                                    className={`py-3 px-4 rounded-md border text-xs font-bold transition-all ${paymentMethod === 'UPI' ? 'bg-indigo-600 text-white border-indigo-600 shadow-md' : 'bg-gray-50 text-gray-500 border-gray-200 hover:bg-gray-100'}`}
                                >
                                    UPI / Wallet
                                </button>
                            </div>
                        </div>

                        <div className="space-y-4">
                            <div className="relative">
                                <FaLock className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-300" />
                                <input type="text" placeholder="Cardholder Name" className="w-full bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none" required />
                            </div>
                            <input type="text" placeholder="Card Number" className="w-full bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none" required />
                            <div className="grid grid-cols-2 gap-4">
                                <input type="text" placeholder="MM/YY" className="bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none" required />
                                <input type="password" placeholder="CVV" className="bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none" required />
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={processing}
                            className={`w-full bg-indigo-600 text-white py-4 rounded-md font-bold hover:bg-indigo-700 transition-all shadow-lg active:scale-95 ${processing ? 'opacity-50 cursor-not-allowed' : ''}`}
                        >
                            {processing ? 'Processing Securely...' : `Pay ₹${booking.totalAmount?.toLocaleString()}`}
                        </button>

                        <p className="text-[10px] text-gray-400 text-center flex items-center justify-center italic">
                            <FaLock className="mr-2" /> All transactions are encrypted and secured.
                        </p>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default Checkout;
