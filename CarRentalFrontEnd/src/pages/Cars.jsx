import React, { useEffect, useState } from 'react';
import { getAllCars } from '../services/carService';
import CarCard from '../components/CarCard';

const Cars = () => {
    const [cars, setCars] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchCars = async () => {
            try {
                const data = await getAllCars();
                if (data.success) {
                    setCars(data.data || []);
                } else {
                    setError('Unable to fetch cars.');
                }
            } catch (err) {
                setError('Error connecting to the car search database.');
            } finally {
                setLoading(false);
            }
        };
        fetchCars();
    }, []);

    return (
        <div className="bg-gray-50 min-h-screen py-12 px-6">
            <div className="container mx-auto">
                <div className="text-center mb-16">
                    <h2 className="text-4xl font-bold text-gray-800">Explore Our Collection</h2>
                    <p className="text-gray-500 mt-4">Find the perfect vehicle for your next adventure</p>
                </div>

                {loading ? (
                    <div className="flex justify-center py-20">
                        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-indigo-600"></div>
                    </div>
                ) : error ? (
                    <div className="bg-red-50 text-red-600 p-4 rounded text-center my-10 border border-red-100">{error}</div>
                ) : cars.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-lg border shadow-sm max-w-lg mx-auto">
                        <p className="text-gray-400">No vehicles are currently available in the fleet.</p>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                        {cars.map(car => (
                            <CarCard key={car.carId} car={car} />
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default Cars;
