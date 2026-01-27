import React, { useEffect, useState } from 'react';
import { getMyBookings, cancelBooking } from '../services/bookingService';
import { Link } from 'react-router-dom';

const MyBookings = () => {
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetchBookings();
    }, []);

    const fetchBookings = async () => {
        try {
            const data = await getMyBookings();
            if (data.success) {
                setBookings(data.data || []);
            }
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleCancel = async (id) => {
        if (!window.confirm("Are you sure you want to cancel this booking?")) return;
        try {
            await cancelBooking(id);
            fetchBookings();
        } catch (err) {
            alert("Failed to cancel the booking.");
        }
    };

    if (loading) return <div className="p-20 text-center">Loading your journey history...</div>;

    return (
        <div className="container mx-auto px-6 py-12 min-h-[70vh]">
            <h2 className="text-3xl font-bold mb-10 text-center">Your Bookings</h2>

            {bookings.length === 0 ? (
                <div className="bg-white border rounded-lg p-12 text-center shadow-sm max-w-lg mx-auto">
                    <p className="text-gray-500 mb-6">You have no bookings yet.</p>
                    <Link to="/" className="inline-block bg-indigo-600 text-white px-6 py-2 rounded font-bold hover:bg-indigo-700">Browse Cars</Link>
                </div>
            ) : (
                <div className="space-y-6 max-w-4xl mx-auto">
                    {bookings.map(booking => (
                        <div key={booking.bookingId} className="bg-white border rounded-lg shadow-sm p-6 flex flex-col md:flex-row justify-between items-start md:items-center">
                            <div>
                                <div className="flex items-center space-x-3 mb-2">
                                    <h3 className="text-xl font-bold text-gray-800">Booking #{booking.bookingId}</h3>
                                    <span className={`px-2 py-1 rounded text-[10px] font-bold uppercase tracking-wider ${booking.bookingStatus === 'CONFIRMED' ? 'bg-green-100 text-green-700' :
                                            booking.bookingStatus === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : 'bg-red-100 text-red-700'
                                        }`}>
                                        {booking.bookingStatus}
                                    </span>
                                </div>
                                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm text-gray-600">
                                    <p><strong>Car:</strong> {booking.car?.brand} {booking.car?.model}</p>
                                    <p><strong>Total:</strong> ₹{booking.totalAmount?.toLocaleString()}</p>
                                    <p><strong>Dates:</strong> {booking.pickupDate} to {booking.dropDate}</p>
                                </div>
                            </div>

                            <div className="mt-4 md:mt-0 flex space-x-3">
                                {booking.bookingStatus === 'PENDING' && (
                                    <button
                                        onClick={() => handleCancel(booking.bookingId)}
                                        className="text-red-600 hover:text-red-800 font-semibold text-sm px-4 py-2 border border-red-200 rounded-md bg-red-50 hover:bg-red-100 transition-colors"
                                    >
                                        Cancel
                                    </button>
                                )}
                                <button className="bg-gray-100 px-4 py-2 rounded-md text-sm font-semibold hover:bg-gray-200 transition-colors">Details</button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyBookings;
