import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { createBooking } from '../services/bookingService';
import { useAuth } from '../context/AuthContext';
import { getCarImage } from '../services/carService';

const BookCar = () => {
    const { carId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [car, setCar] = useState(null);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({
        pickupDate: '',
        dropDate: '',
        location: '',
        withDriver: false
    });
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchCar = async () => {
            try {
                const { default: api } = await import('../services/api');
                const response = await api.get(`/cars/${carId}`);
                if (response.data.success) {
                    setCar(response.data.data);
                }
            } catch (err) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchCar();
    }, [carId]);

    const handleChange = (e) => {
        const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
        setFormData({ ...formData, [e.target.name]: value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const pDate = new Date(formData.pickupDate);
        const dDate = new Date(formData.dropDate);

        if (!formData.pickupDate || !formData.dropDate) {
            setError("Please fill in all dates.");
            return;
        }

        if (pDate < today) {
            setError("Pickup date cannot be in the past.");
            return;
        }

        if (dDate < pDate) {
            setError("Return date must be on or after the pickup date.");
            return;
        }

        if (!formData.location || formData.location.trim().length === 0) {
            setError("Please specify a pickup location.");
            return;
        }

        try {
            const bookingPayload = {
                carId: parseInt(carId),
                pickupDate: formData.pickupDate,
                dropDate: formData.dropDate
            };

            const data = await createBooking(bookingPayload);
            if (data.success) {
                // Redirecting to payment checkout
                navigate(`/checkout/${data.data.bookingId}`);
            } else {
                setError(data.message || "Booking failed.");
            }
        } catch (err) {
            setError("An error occurred while booking.");
        }
    };

    if (loading) return <div className="p-20 text-center">Loading car details...</div>;
    if (!car) return <div className="p-20 text-center">Car not found.</div>;

    const imageUrl = car.image
        ? `data:image/jpeg;base64,${car.image}`
        : getCarImage(car.brand);

    return (
        <div className="container mx-auto px-6 py-12">
            <h2 className="text-3xl font-bold mb-8">Book {car.brand} {car.model}</h2>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
                <div>
                    <img src={imageUrl} alt="Car" className="w-full h-96 object-cover rounded-lg shadow-md mb-6" />
                    <div className="bg-white p-6 rounded-lg border border-gray-200">
                        <h3 className="text-xl font-bold mb-4">Car Information</h3>
                        <div className="space-y-2 text-gray-700">
                            <p><strong>Type:</strong> {car.carType}</p>
                            <p><strong>Fuel:</strong> {car.fuelType}</p>
                            <p><strong>City:</strong> {car.city}</p>
                            <p><strong>Price:</strong> ₹{car.pricePerDay?.toLocaleString()} / day</p>
                            <p className="mt-4">{car.description}</p>
                        </div>
                    </div>
                </div>

                <div className="bg-white p-8 rounded-lg border border-gray-200 shadow-sm h-fit">
                    <h3 className="text-xl font-bold mb-6">Reservation Details</h3>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        {error && <div className="bg-red-50 text-red-600 p-3 rounded text-sm mb-4 border border-red-100">{error}</div>}

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Pickup Date</label>
                            <input type="date" name="pickupDate" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" onChange={handleChange} required />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Return Date</label>
                            <input type="date" name="dropDate" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" onChange={handleChange} required />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Pickup Location</label>
                            <input type="text" name="location" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Enter address" onChange={handleChange} />
                        </div>

                        <div className="flex items-center">
                            <input type="checkbox" name="withDriver" id="driver" className="h-4 w-4 text-indigo-600 border-gray-300 rounded focus:ring-indigo-500" onChange={handleChange} />
                            <label htmlFor="driver" className="ml-2 block text-sm text-gray-700">Include professional driver (+₹1,000)</label>
                        </div>

                        <button type="submit" className="w-full bg-indigo-600 text-white py-3 rounded-md font-bold hover:bg-indigo-700 transition-colors shadow-sm">
                            Confirm Booking
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default BookCar;
