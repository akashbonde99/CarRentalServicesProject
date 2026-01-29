import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { getBookingById, cancelBooking } from '../services/bookingService';
import { getPaymentByBookingId } from '../services/paymentService';
import { FaCalendarAlt, FaCar, FaCreditCard, FaCheckCircle, FaExclamationCircle } from 'react-icons/fa';

const BookingDetails = () => {
    const { bookingId } = useParams();
    const navigate = useNavigate();
    const [booking, setBooking] = useState(null);
    const [payment, setPayment] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchData = async () => {
            try {
                // Fetch Booking Details
                const bookingResponse = await getBookingById(bookingId);
                if (bookingResponse.success) {
                    setBooking(bookingResponse.data);
                } else {
                    setError('Failed to load booking details.');
                }

                // Fetch Payment Details (might be null if not paid)
                try {
                    const paymentResponse = await getPaymentByBookingId(bookingId);
                    if (paymentResponse.success) {
                        setPayment(paymentResponse.data);
                    }
                } catch (err) {
                    console.log("Payment info not found or error:", err);
                    setPayment(null);
                }

            } catch (err) {
                console.error(err);
                setError('Could not retrieve booking details.');
            } finally {
                setLoading(false);
            }
        };

        fetchData();
        fetchData();
    }, [bookingId]);

    const handleCancelBooking = async () => {
        if (!window.confirm("Are you sure you want to cancel this booking?")) return;
        try {
            await cancelBooking(bookingId);
            alert("Booking cancelled successfully.");
            // Refresh details
            const updated = await getBookingById(bookingId);
            if (updated.success) setBooking(updated.data);
        } catch (err) {
            alert("Failed to cancel booking: " + err.message);
        }
    };

    if (loading) return <div className="p-20 text-center text-gray-600">Loading details...</div>;
    if (error) return <div className="p-20 text-center text-red-600 font-bold">{error}</div>;
    if (!booking) return <div className="p-20 text-center">Booking not found.</div>;

    const isPaid = payment?.paymentStatus === 'SUCCESS';
    const isConfirmed = booking.bookingStatus === 'CONFIRMED';

    return (
        <div className="container mx-auto px-6 py-12 max-w-5xl">
            <div className="flex justify-between items-center mb-8">
                <h2 className="text-3xl font-bold text-gray-800">Booking Details #{booking.bookingId}</h2>
                <Link to="/my-bookings" className="text-indigo-600 hover:text-indigo-800 font-semibold">
                    ← Back to My Bookings
                </Link>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">

                {/* Left Column: Car Details */}
                <div className="md:col-span-2 space-y-8">
                    {/* Car Card */}
                    <div className="bg-white rounded-lg shadow-sm border overflow-hidden">
                        <div className="h-64 bg-gray-200 w-full object-cover">
                            {booking.car?.image ? (
                                <img src={`data:image/jpeg;base64,${booking.car.image}`} alt={booking.car.model} className="w-full h-full object-cover" />
                            ) : (
                                <div className="flex items-center justify-center h-full text-gray-400">No Image Available</div>
                            )}
                        </div>
                        <div className="p-6">
                            <h3 className="text-2xl font-bold mb-2">{booking.car?.brand} {booking.car?.model}</h3>
                            <p className="text-gray-600 mb-4">{booking.car?.description}</p>

                            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 text-sm text-gray-600">
                                <div className="bg-gray-50 p-3 rounded">
                                    <span className="block text-xs text-gray-400 uppercase">Fuel</span>
                                    <span className="font-semibold">{booking.car?.fuelType}</span>
                                </div>
                                <div className="bg-gray-50 p-3 rounded">
                                    <span className="block text-xs text-gray-400 uppercase">Type</span>
                                    <span className="font-semibold">{booking.car?.carType}</span>
                                </div>
                                <div className="bg-gray-50 p-3 rounded">
                                    <span className="block text-xs text-gray-400 uppercase">Seats</span>
                                    <span className="font-semibold">{booking.car?.seatingCapacity || 5}</span>
                                </div>
                                <div className="bg-gray-50 p-3 rounded">
                                    <span className="block text-xs text-gray-400 uppercase">Location</span>
                                    <span className="font-semibold">{booking.car?.city}</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Booking Timeline */}
                    <div className="bg-white rounded-lg shadow-sm border p-6">
                        <h3 className="text-lg font-bold mb-4 flex items-center">
                            <FaCalendarAlt className="mr-2 text-indigo-600" /> Itinerary
                        </h3>
                        <div className="flex flex-col sm:flex-row justify-between items-center space-y-4 sm:space-y-0 text-center sm:text-left">
                            <div>
                                <p className="text-xs text-gray-500 uppercase">Pickup</p>
                                <p className="text-lg font-bold text-gray-800">{booking.pickupDate}</p>
                                <p className="text-sm text-gray-600">{booking.car?.pickupAddress}</p>
                            </div>
                            <div className="text-gray-300 hidden sm:block">-----------------&gt;</div>
                            <div className="text-right">
                                <p className="text-xs text-gray-500 uppercase">Return</p>
                                <p className="text-lg font-bold text-gray-800">{booking.dropDate}</p>
                                <p className="text-sm text-gray-600">{booking.car?.city}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right Column: Status & Payment */}
                <div className="space-y-8">
                    {/* Booking Status */}
                    <div className="bg-white rounded-lg shadow-sm border p-6">
                        <h3 className="text-lg font-bold mb-4">Reservation Status</h3>
                        <div className={`p-4 rounded-lg flex items-center ${isConfirmed ? 'bg-green-50 text-green-700' :
                            booking.bookingStatus === 'PENDING' ? 'bg-yellow-50 text-yellow-700' : 'bg-red-50 text-red-700'
                            }`}>
                            {isConfirmed ? <FaCheckCircle className="mr-3 text-xl" /> : <FaExclamationCircle className="mr-3 text-xl" />}
                            <div>
                                <p className="font-bold">{booking.bookingStatus}</p>
                                <p className="text-xs opacity-80">
                                    {isConfirmed ? "Your booking is confirmed." : "Waiting for admin approval."}
                                </p>
                            </div>
                        </div>
                    </div>

                    {/* Payment Info */}
                    <div className="bg-white rounded-lg shadow-sm border p-6">
                        <h3 className="text-lg font-bold mb-4 flex items-center">
                            <FaCreditCard className="mr-2 text-indigo-600" /> Payment Details
                        </h3>

                        <div className="space-y-3 text-sm">
                            <div className="flex justify-between">
                                <span className="text-gray-500">Rate per Day</span>
                                <span>₹{booking.car?.pricePerDay}</span>
                            </div>
                            <div className="flex justify-between pt-2 border-t font-bold text-lg">
                                <span>Total Amount</span>
                                <span className="text-indigo-600">₹{booking.totalAmount}</span>
                            </div>
                        </div>

                        <div className="mt-6 pt-6 border-t">
                            <h4 className="font-bold text-gray-700 mb-2 text-sm">Payment Status</h4>
                            <div className={`flex items-center mb-4 ${isPaid ? 'text-green-600' : 'text-orange-500'}`}>
                                <span className={`w-3 h-3 rounded-full mr-2 ${isPaid ? 'bg-green-500' : 'bg-orange-500'}`}></span>
                                <span className="font-bold">{payment?.paymentStatus || booking.paymentStatus || 'PENDING'}</span>
                            </div>

                            {isPaid ? (
                                <div className="text-xs text-gray-500 bg-gray-50 p-3 rounded">
                                    <p>Method: {payment?.paymentMode || 'Online'}</p>
                                    <p>Date: {payment?.paymentDate}</p>
                                    <p title={payment?.razorpayPaymentId} className="truncate">Ref: {payment?.razorpayPaymentId}</p>
                                </div>
                            ) : (
                                <div className="space-y-3">
                                    {isConfirmed ? (
                                        <Link to={`/checkout/${booking.bookingId}`} className="block w-full bg-indigo-600 text-white text-center py-3 rounded-md font-bold hover:bg-indigo-700 transition">
                                            Pay Now
                                        </Link>
                                    ) : (
                                        <p className="text-xs text-gray-400 italic text-center">
                                            Payment will be enabled once the booking is confirmed by Admin.
                                        </p>
                                    )}

                                    {['PENDING', 'CONFIRMED'].includes(booking.bookingStatus) && (
                                        <button
                                            onClick={handleCancelBooking}
                                            className="block w-full bg-red-50 text-red-600 border border-red-200 py-3 rounded-md font-bold hover:bg-red-100 transition"
                                        >
                                            Cancel Booking
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default BookingDetails;
