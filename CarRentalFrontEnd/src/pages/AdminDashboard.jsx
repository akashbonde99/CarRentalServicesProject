import React, { useState, useEffect } from 'react';
import { getAllCars, addCar, deleteCar } from '../services/carService';
import { getAllBookings, updateBookingStatus } from '../services/bookingService';
import { getPendingAdmins, approveAdmin } from '../services/authService';
import { FaPlus, FaTrash, FaCheck, FaTimes, FaMapMarkerAlt } from 'react-icons/fa';

const AdminDashboard = () => {
    const [activeTab, setActiveTab] = useState('cars');
    const [cars, setCars] = useState([]);
    const [bookings, setBookings] = useState([]);
    const [pendingAdmins, setPendingAdmins] = useState([]);
    const [loading, setLoading] = useState(true);
    const [viewLicense, setViewLicense] = useState(null);

    const [showAddModal, setShowAddModal] = useState(false);
    const [carForm, setCarForm] = useState({
        brand: '', model: '', registrationNumber: '', city: '',
        pickupAddress: '', mapUrl: '', description: '', pricePerDay: '', seatingCapacity: '',
        fuelType: 'PETROL', carType: 'SEDAN', image: null
    });

    useEffect(() => {
        if (activeTab === 'cars') fetchCars();
        else if (activeTab === 'bookings') fetchBookings();
        else if (activeTab === 'admins') fetchPendingAdmins();
    }, [activeTab]);

    const fetchCars = async () => {
        setLoading(true);
        try {
            const data = await getAllCars();
            if (data.success) setCars(data.data || []);
        } catch (err) { console.error(err); }
        finally { setLoading(false); }
    };

    const fetchBookings = async () => {
        setLoading(true);
        try {
            const data = await getAllBookings();
            if (data.success) setBookings(data.data || []);
        } catch (err) { console.error(err); }
        finally { setLoading(false); }
    };

    const fetchPendingAdmins = async () => {
        setLoading(true);
        try {
            const data = await getPendingAdmins();
            if (data.success) setPendingAdmins(data.data || []);
        } catch (err) { console.error(err); }
        finally { setLoading(false); }
    };

    const handleDeleteCar = async (id) => {
        if (!window.confirm("Are you sure you want to delete this car?")) return;
        try {
            await deleteCar(id);
            fetchCars();
        } catch (err) { alert("Failed to delete car."); }
    };

    const handleCarFormChange = (e) => {
        if (e.target.name === 'image') {
            setCarForm({ ...carForm, image: e.target.files[0] });
        } else {
            setCarForm({ ...carForm, [e.target.name]: e.target.value });
        }
    };

    const handleAddCar = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        Object.keys(carForm).forEach(key => {
            if (key === 'image') {
                formData.append('imageFile', carForm[key]);
            } else {
                formData.append(key, carForm[key]);
            }
        });

        try {
            console.log("Adding car with data:", Object.fromEntries(formData.entries()));
            const res = await addCar(formData);
            if (res.success) {
                setShowAddModal(false);
                fetchCars();
            } else {
                alert(res.message);
            }
        } catch (err) {
            alert("Failed to add car.");
        }
    };

    const handleStatusUpdate = async (id, status) => {
        try {
            await updateBookingStatus(id, status);
            fetchBookings();
        } catch (err) { alert("Failed to update status."); }
    };

    const handleApproveAdmin = async (adminId) => {
        if (!window.confirm("Approve this admin? They will gain full admin access.")) return;
        try {
            const res = await approveAdmin(adminId);
            if (res.success) {
                // Refresh pending list
                fetchPendingAdmins();
            } else {
                alert(res.message || "Failed to approve admin.");
            }
        } catch (err) {
            alert("Failed to approve admin.");
        }
    };

    return (
        <div className="bg-gray-100 min-h-screen p-8">
            <div className="max-w-6xl mx-auto">
                <div className="flex justify-between items-center mb-10">
                    <h1 className="text-3xl font-bold text-gray-800">Admin Dashboard</h1>
                    <button
                        onClick={() => setShowAddModal(true)}
                        className="bg-indigo-600 text-white px-6 py-2 rounded-md font-bold hover:bg-indigo-700 transition-colors flex items-center shadow-sm"
                    >
                        <FaPlus className="mr-2" /> Add New Car
                    </button>
                </div>

                {/* Tabs */}
                <div className="flex space-x-8 mb-8 border-b border-gray-300">
                    <button
                        className={`pb-4 px-2 font-bold text-sm transition-all ${activeTab === 'cars' ? 'border-b-2 border-indigo-600 text-indigo-600' : 'text-gray-500 hover:text-gray-700'}`}
                        onClick={() => setActiveTab('cars')}
                    >
                        Cars Inventory
                    </button>
                    <button
                        className={`pb-4 px-2 font-bold text-sm transition-all ${activeTab === 'bookings' ? 'border-b-2 border-indigo-600 text-indigo-600' : 'text-gray-500 hover:text-gray-700'}`}
                        onClick={() => setActiveTab('bookings')}
                    >
                        Customer Bookings
                    </button>
                    <button
                        className={`pb-4 px-2 font-bold text-sm transition-all ${activeTab === 'admins' ? 'border-b-2 border-indigo-600 text-indigo-600' : 'text-gray-500 hover:text-gray-700'}`}
                        onClick={() => setActiveTab('admins')}
                    >
                        Admin Requests
                    </button>
                </div>

                {/* Content */}
                {activeTab === 'cars' && (
                    <div className="bg-white rounded-lg shadow-md border overflow-hidden">
                        <table className="w-full text-left">
                            <thead className="bg-gray-50 border-b">
                                <tr className="text-gray-700 uppercase text-xs font-bold">
                                    <th className="p-4 leading-4">ID</th>
                                    <th className="p-4 leading-4">Brand & Model</th>
                                    <th className="p-4 leading-4">Registration</th>
                                    <th className="p-4 leading-4">Price/Day</th>
                                    <th className="p-4 leading-4">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y text-sm">
                                {cars.map(car => (
                                    <tr key={car.carId} className="hover:bg-gray-50">
                                        <td className="p-4 font-medium">#{car.carId}</td>
                                        <td className="p-4 font-bold">{car.brand} {car.model}</td>
                                        <td className="p-4 text-gray-600">{car.registrationNumber}</td>
                                        <td className="p-4 font-bold text-indigo-600">₹{car.pricePerDay?.toLocaleString()}</td>
                                        <td className="p-4">
                                            <button
                                                onClick={() => handleDeleteCar(car.carId)}
                                                className="text-red-600 hover:text-red-800 transition-all p-2 hover:bg-red-50 rounded"
                                            >
                                                <FaTrash />
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {activeTab === 'bookings' && (
                    <div className="bg-white rounded-lg shadow-md border overflow-hidden">
                        <table className="w-full text-left">
                            <thead className="bg-gray-50 border-b">
                                <tr className="text-gray-700 uppercase text-xs font-bold">
                                    <th className="p-4 leading-4">ID</th>
                                    <th className="p-4 leading-4">Customer</th>
                                    <th className="p-4 leading-4">Car</th>
                                    <th className="p-4 leading-4">Dates</th>
                                    <th className="p-4 leading-4">Total</th>
                                    <th className="p-4 leading-4">Payment</th>
                                    <th className="p-4 leading-4">Status</th>
                                    <th className="p-4 leading-4">License</th>
                                    <th className="p-4 leading-4">Action</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y text-sm">
                                {bookings.map(booking => (
                                    <tr key={booking.bookingId} className="hover:bg-gray-50">
                                        <td className="p-4 font-medium">#{booking.bookingId}</td>
                                        <td className="p-4 font-semibold">{booking.user?.name}</td>
                                        <td className="p-4">{booking.car?.brand} {booking.car?.model}</td>
                                        <td className="p-4 text-gray-500">{booking.pickupDate} - {booking.dropDate}</td>
                                        <td className="p-4 font-bold">₹{booking.totalAmount?.toLocaleString()}</td>
                                        <td className="p-4">
                                            <span className={`text-[10px] font-bold uppercase ${booking.paymentStatus === 'SUCCESS' ? 'text-green-600' : 'text-orange-500'}`}>
                                                {booking.paymentStatus || 'PENDING'}
                                            </span>
                                        </td>
                                        <td className="p-4">
                                            <span className={`px-2 py-1 rounded text-[10px] font-bold uppercase ${booking.bookingStatus === 'CONFIRMED' ? 'bg-green-100 text-green-700' :
                                                booking.bookingStatus === 'REJECTED' ? 'bg-red-100 text-red-700' : 'bg-yellow-100 text-yellow-700'
                                                }`}>
                                                {booking.bookingStatus}
                                            </span>
                                        </td>
                                        <td className="p-4">
                                            <button
                                                onClick={() => {
                                                    if (booking.user?.drivingLicenceImage) {
                                                        setViewLicense(booking.user.drivingLicenceImage);
                                                    } else {
                                                        alert("No license image uploaded for this user.");
                                                    }
                                                }}
                                                className="text-indigo-600 hover:text-indigo-900 underline text-xs"
                                            >
                                                View DL
                                            </button>
                                        </td>
                                        <td className="p-4 text-right">
                                            {booking.bookingStatus === 'PENDING' && (
                                                <div className="flex space-x-2">
                                                    <button
                                                        onClick={() => handleStatusUpdate(booking.bookingId, 'CONFIRMED')}
                                                        className="p-1.5 bg-green-50 text-green-600 rounded hover:bg-green-600 hover:text-white transition-all"
                                                        title="Approve"
                                                    >
                                                        <FaCheck size={14} />
                                                    </button>
                                                    <button
                                                        onClick={() => handleStatusUpdate(booking.bookingId, 'REJECTED')}
                                                        className="p-1.5 bg-red-50 text-red-600 rounded hover:bg-red-600 hover:text-white transition-all"
                                                        title="Reject"
                                                    >
                                                        <FaTimes size={14} />
                                                    </button>
                                                </div>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {activeTab === 'admins' && (
                    <div className="bg-white rounded-lg shadow-md border overflow-hidden">
                        <table className="w-full text-left">
                            <thead className="bg-gray-50 border-b">
                                <tr className="text-gray-700 uppercase text-xs font-bold">
                                    <th className="p-4 leading-4">ID</th>
                                    <th className="p-4 leading-4">Name</th>
                                    <th className="p-4 leading-4">Email</th>
                                    <th className="p-4 leading-4">Driving Licence</th>
                                    <th className="p-4 leading-4 text-right">Action</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y text-sm">
                                {pendingAdmins.length === 0 && (
                                    <tr>
                                        <td className="p-6 text-center text-gray-500 text-sm" colSpan={5}>
                                            No pending admin requests.
                                        </td>
                                    </tr>
                                )}
                                {pendingAdmins.map((admin) => (
                                    <tr key={admin.userId} className="hover:bg-gray-50">
                                        <td className="p-4 font-medium">#{admin.userId}</td>
                                        <td className="p-4 font-semibold">{admin.name}</td>
                                        <td className="p-4 text-gray-600">{admin.email}</td>
                                        <td className="p-4 text-gray-600">{admin.drivingLicence || '—'}</td>
                                        <td className="p-4 text-right">
                                            <button
                                                onClick={() => handleApproveAdmin(admin.userId)}
                                                className="inline-flex items-center px-3 py-1.5 bg-green-600 text-white text-xs font-bold rounded hover:bg-green-700 transition-all"
                                            >
                                                <FaCheck className="mr-1" /> Approve
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Add Car Modal */}
            {showAddModal && (
                <div className="fixed inset-0 bg-gray-900/50 flex items-center justify-center p-6 z-[60]">
                    <div className="bg-white rounded-lg p-8 w-full max-w-2xl max-h-[90vh] overflow-y-auto shadow-xl">
                        <h2 className="text-2xl font-bold mb-6 text-gray-800">Add New Vehicle</h2>
                        <form onSubmit={handleAddCar} className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Brand</label>
                                <input name="brand" placeholder="e.g. Toyota" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Model</label>
                                <input name="model" placeholder="e.g. Camry" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Registration #</label>
                                <input name="registrationNumber" placeholder="ABC-1234" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">City</label>
                                <input name="city" placeholder="Mumbai" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Pickup Address</label>
                                <input name="pickupAddress" placeholder="Full address" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Google Maps URL</label>
                                <input name="mapUrl" value={carForm.mapUrl} placeholder="https://www.google.com/maps/..." onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" />
                                <p className="text-[10px] text-gray-400 mt-1 italic">Optional: Paste a link to the exact pickup location.</p>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Price Per Day</label>
                                <input name="pricePerDay" type="number" placeholder="1500" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Fuel Type</label>
                                <select name="fuelType" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 bg-white">
                                    <option value="PETROL">Petrol</option>
                                    <option value="DIESEL">Diesel</option>
                                    <option value="ELECTRIC">Electric</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Car Type</label>
                                <select name="carType" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 bg-white">
                                    <option value="SEDAN">Sedan</option>
                                    <option value="SUV">SUV</option>
                                    <option value="HATCHBACK">Hatchback</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Seating Capacity</label>
                                <input name="seatingCapacity" type="number" placeholder="5" onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" required />
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                                <textarea name="description" placeholder="Brief description..." onChange={handleCarFormChange} className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500" rows="3"></textarea>
                            </div>
                            <div className="md:col-span-2">
                                <label className="block text-sm font-medium text-gray-700 mb-1">Image File</label>
                                <input type="file" name="image" onChange={handleCarFormChange} className="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100" />
                            </div>
                            <div className="md:col-span-2 flex justify-end space-x-4 mt-6">
                                <button type="button" onClick={() => setShowAddModal(false)} className="text-gray-500 font-bold hover:text-gray-700">Cancel</button>
                                <button type="submit" className="bg-indigo-600 text-white px-8 py-2 rounded-md font-bold hover:bg-indigo-700 shadow-md">Add Car</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* License View Modal */}
            {viewLicense && (
                <div className="fixed inset-0 bg-gray-900/80 flex items-center justify-center p-6 z-[70]" onClick={() => setViewLicense(null)}>
                    <div className="bg-white rounded-lg p-4 max-w-3xl max-h-[90vh] overflow-auto shadow-2xl relative" onClick={e => e.stopPropagation()}>
                        <button onClick={() => setViewLicense(null)} className="absolute top-2 right-2 text-gray-500 hover:text-gray-800 text-2xl font-bold">&times;</button>
                        <h3 className="text-xl font-bold mb-4">Driving License</h3>
                        <img src={`data:image/jpeg;base64,${viewLicense}`} alt="Driving License" className="max-w-full h-auto rounded" />
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;
