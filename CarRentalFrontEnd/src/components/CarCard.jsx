import React from 'react';
import { Link } from 'react-router-dom';
import { getCarImage } from '../services/carService';
import { FaGasPump, FaMapMarkerAlt } from 'react-icons/fa';
import { useAuth } from '../context/AuthContext';

const CarCard = ({ car }) => {
    const { user } = useAuth();
    // Keep the fix for defensive check in pricePerDay
    const imageUrl = car.image
        ? `data:image/jpeg;base64,${car.image}`
        : getCarImage(car.brand);

    return (
        <div className="bg-white rounded-lg shadow-md border border-gray-200 overflow-hidden flex flex-col group hover:shadow-lg transition-shadow">
            <div className="relative h-48">
                <img
                    src={imageUrl}
                    alt={`${car.brand} ${car.model}`}
                    className="w-full h-full object-cover"
                />
                <div className="absolute top-2 right-2 bg-indigo-600 text-white text-[10px] px-2 py-1 rounded font-bold uppercase">
                    {car.carType}
                </div>
            </div>

            <div className="p-5 flex-grow flex flex-col">
                <h3 className="text-lg font-bold text-gray-800 mb-2">
                    {car.brand} {car.model}
                </h3>

                <div className="flex items-center text-gray-500 text-xs space-x-4 mb-4">
                    <span className="flex items-center"><FaGasPump className="mr-1 text-indigo-500" /> {car.fuelType}</span>
                    <span className="flex items-center"><FaMapMarkerAlt className="mr-1 text-indigo-500" /> {car.city}</span>
                </div>

                <div className="mt-auto flex justify-between items-center pt-4 border-t border-gray-100">
                    <div>
                        <span className="text-xl font-bold text-indigo-600">₹{car.pricePerDay?.toLocaleString()}</span>
                        <span className="text-gray-400 text-[10px] ml-1">/ day</span>
                    </div>
                    <Link
                        to={user ? `/book/${car.carId}` : "/login"}
                        className="bg-indigo-600 text-white px-4 py-2 rounded text-sm font-semibold hover:bg-indigo-700 transition-colors"
                    >
                        Rent Now
                    </Link>
                </div>
            </div>
        </div>
    );
};

export default CarCard;
