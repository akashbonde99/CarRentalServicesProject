import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerUser } from '../services/authService';

const Register = () => {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: '',
        role: 'CUSTOMER',
        drivingLicence: ''
    });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!formData.name.trim()) {
            setError("Please enter your full name.");
            return;
        }

        if (!emailRegex.test(formData.email)) {
            setError("Please enter a valid email address.");
            return;
        }

        if (formData.password.length < 6) {
            setError("Password must be at least 6 characters long.");
            return;
        }

        if (formData.password !== formData.confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        try {
            const data = await registerUser({
                name: formData.name.trim(),
                email: formData.email.trim(),
                password: formData.password,
                role: formData.role,
                drivingLicence: formData.drivingLicence ? formData.drivingLicence.trim() : null
            });

            if (data.success) {
                navigate('/login');
            } else {
                setError(data.message || 'Registration failed');
            }
        } catch (err) {
            setError('Error connecting to the registration service.');
        }
    };

    return (
        <div className="flex items-center justify-center min-h-[90vh] bg-gray-100 py-12">
            <div className="bg-white p-10 rounded-lg shadow-md w-full max-w-xl border border-gray-200">
                <div className="text-center mb-10">
                    <h2 className="text-3xl font-bold text-gray-800 tracking-tight text-center">Create Account</h2>
                    <p className="text-gray-500 mt-2">Join RentCars today</p>
                </div>

                {error && <div className="bg-red-50 border border-red-100 text-red-600 p-4 rounded mb-8 text-sm text-center font-medium">{error}</div>}

                <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="md:col-span-2">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                        <input type="text" name="name" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="John Doe" onChange={handleChange} required />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                        <input type="email" name="email" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="john@example.com" onChange={handleChange} required />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Driving License</label>
                        <input type="text" name="drivingLicence" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Optional" onChange={handleChange} />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        <input type="password" name="password" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Create password" onChange={handleChange} required />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                        <input type="password" name="confirmPassword" className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500" placeholder="Repeat password" onChange={handleChange} required />
                    </div>

                    <div className="md:col-span-2">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Account Type</label>
                        <select
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500 bg-white"
                        >
                            <option value="CUSTOMER">Customer</option>
                            <option value="ADMIN">Admin</option>
                        </select>
                    </div>

                    <div className="md:col-span-2 mt-4">
                        <button type="submit" className="w-full bg-indigo-600 text-white py-3 rounded-md font-bold hover:bg-indigo-700 shadow-sm transition-all text-sm">
                            Create Account
                        </button>
                    </div>

                    <div className="md:col-span-2 text-center mt-6 text-sm">
                        <span className="text-gray-500">Already have an account? </span>
                        <Link to="/login" className="text-indigo-600 font-bold hover:underline">Sign In</Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Register;
