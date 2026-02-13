import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllCars } from '../services/carService';
import CarCard from '../components/CarCard';

const Home = () => {
    // State to hold all cars and the filtered list (for search results)
    const [cars, setCars] = useState([]);
    const [filteredCars, setFilteredCars] = useState([]);

    // Dropdown options for cities
    const [cities, setCities] = useState([]);

    // UI states
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Search form state
    const [searchParams, setSearchParams] = useState({
        location: '',
        pickupDate: '',
        dropDate: ''
    });

    // When the page loads, we fetch all cars and valid cities from the backend.
    useEffect(() => {
        const fetchHomeData = async () => {
            try {
                // Fetch all cars to ensure user's test data is visible
                const carsRes = await getAllCars();

                // Fetch unique cities for the dropdown
                const { default: api } = await import('../services/api');
                const citiesRes = await api.get('/cars/cities');

                if (carsRes.success) {
                    const allCars = carsRes.data || [];
                    setCars(allCars);
                    setFilteredCars(allCars); // Initially, show everything
                }

                if (citiesRes.data.success) {
                    setCities(citiesRes.data.data || []);
                }

            } catch (err) {
                setError('Error connecting to server.');
            } finally {
                setLoading(false);
            }
        };
        fetchHomeData();
    }, []);

    const handleSearchChange = (e) => {
        setSearchParams({
            ...searchParams,
            [e.target.name]: e.target.value
        });
    };

    // The logic to filter cars based on user input.
    // If they select dates, we need to ask the backend "Which cars are free?".
    const handleSearch = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            // Using dynamic import to avoid circular dependency issues if any
            const { searchCars } = await import('../services/carService');

            const params = {
                location: searchParams.location,
                pickupDate: searchParams.pickupDate,
                dropDate: searchParams.dropDate
            };

            const response = await searchCars(params);

            if (response.success) {
                // Attach key booking details to each car object so CarCard can pass them
                const carsWithDetails = (response.data || []).map(c => ({
                    ...c,
                    bookingDetails: {
                        pickupDate: searchParams.pickupDate,
                        dropDate: searchParams.dropDate,
                        location: searchParams.location
                    }
                }));
                setFilteredCars(carsWithDetails);
            } else {
                setError("No cars found or search failed.");
            }
        } catch (err) {
            console.error(err);
            setError("Error searching for cars.");
        } finally {
            setLoading(false);
        }
    };

    const handleReset = () => {
        setSearchParams({ location: '', pickupDate: '', dropDate: '' });
        // Reset to show all cars again
        setFilteredCars(cars);
    };

    return (
        <div className="bg-gray-50 min-h-screen">
            {/* Simple Hero Section */}
            <header className="bg-gray-900 text-white py-20 px-6">
                <div className="container mx-auto text-center max-w-3xl">
                    <h1 className="text-4xl md:text-5xl font-bold mb-6">
                        Rent the Best Cars at Affordable Prices
                    </h1>
                    <p className="text-gray-400 text-lg mb-10">
                        Choose from our wide collection of premium vehicles for your next journey. Fast, reliable, and convenient.
                    </p>
                    <a href="#fleet" className="bg-indigo-600 hover:bg-indigo-700 text-white px-8 py-3 rounded-md font-bold transition-colors">
                        Explore Collection
                    </a>
                </div>
            </header>

            {/* Advanced Search Bar */}
            <div className="container mx-auto px-6 -mt-10 relative z-10">
                <form onSubmit={handleSearch} className="bg-white rounded-lg shadow-xl p-8 grid grid-cols-1 md:grid-cols-4 gap-6 items-end border border-gray-100">
                    <div>
                        <label className="block text-xs font-black uppercase tracking-widest text-gray-400 mb-2">Location</label>
                        <select
                            name="location"
                            className="w-full bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none appearance-none"
                            value={searchParams.location}
                            onChange={handleSearchChange}
                        >
                            <option value="">All Regions</option>
                            {cities.map(city => (
                                <option key={city} value={city}>{city}</option>
                            ))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-xs font-black uppercase tracking-widest text-gray-400 mb-2">Pickup Date</label>
                        <input
                            name="pickupDate"
                            type="date"
                            className="w-full bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
                            value={searchParams.pickupDate}
                            onChange={handleSearchChange}
                        />
                    </div>
                    <div>
                        <label className="block text-xs font-black uppercase tracking-widest text-gray-400 mb-2">Return Date</label>
                        <input
                            name="dropDate"
                            type="date"
                            className="w-full bg-gray-50 border-0 rounded-md px-4 py-3 text-sm focus:ring-2 focus:ring-indigo-500 outline-none"
                            value={searchParams.dropDate}
                            onChange={handleSearchChange}
                        />
                    </div>
                    <button
                        type="submit"
                        className="bg-indigo-600 hover:bg-indigo-700 text-white font-bold py-3 rounded-md transition-all shadow-lg shadow-indigo-200"
                    >
                        Search Fleet
                    </button>
                </form>
            </div>

            {/* List Section */}
            <section id="fleet" className="container mx-auto px-6 py-16">
                <div className="flex justify-between items-center mb-10 border-b pb-4">
                    <div className="flex items-baseline space-x-3">
                        <h2 className="text-2xl font-bold text-gray-800 tracking-tight">Available Vehicles</h2>
                        <span className="text-xs text-gray-400 font-bold uppercase">({filteredCars.length} found)</span>
                    </div>
                    <Link to="/cars" className="text-indigo-600 hover:text-indigo-800 font-semibold text-sm">View All →</Link>
                </div>

                {loading ? (
                    <div className="flex justify-center py-10">
                        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-indigo-600"></div>
                    </div>
                ) : error ? (
                    <div className="bg-red-50 text-red-600 p-4 rounded text-center my-10 border border-red-100">{error}</div>
                ) : filteredCars.length === 0 ? (
                    <div className="text-center py-20 bg-white rounded-lg border border-gray-100 shadow-sm">
                        <div className="mb-4 text-gray-300 flex justify-center">
                            <svg className="w-16 h-16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M9.172 9.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                            </svg>
                        </div>
                        <p className="text-gray-500 font-medium">No vehicles found in "{searchParams.location || 'your location'}"</p>
                        <p className="text-gray-400 text-sm mt-1">Try searching for a different city or clearing the filters.</p>
                        <button
                            onClick={handleReset}
                            className="mt-6 bg-indigo-50 text-indigo-600 px-6 py-2 rounded-md font-bold hover:bg-indigo-100 transition-all border border-indigo-100"
                        >
                            Show All Available Cars
                        </button>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {filteredCars.map(car => (
                            <CarCard key={car.carId} car={car} />
                        ))}
                    </div>
                )}
            </section>
        </div>
    );
};

export default Home;
