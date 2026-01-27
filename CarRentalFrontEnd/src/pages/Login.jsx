import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { loginUser } from '../services/authService';
import { useAuth } from '../context/AuthContext';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const data = await loginUser({ email, password });
            if (data.success) {
                login(data.data.user || data.data, data.data.token);
                navigate('/');
            } else {
                setError(data.message || 'Login failed');
            }
        } catch (err) {
            setError('Could not connect to the authentication server.');
        }
    };

    return (
        <div className="flex items-center justify-center min-h-[80vh] bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-full max-w-md border border-gray-200">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-gray-800">Login</h2>
                    <p className="text-gray-500 mt-2">Welcome back to RentCars</p>
                </div>

                {error && <div className="bg-red-50 border border-red-100 text-red-600 p-3 rounded mb-6 text-sm text-center">{error}</div>}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email Address</label>
                        <input
                            type="email"
                            className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="Enter your email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        <input
                            type="password"
                            className="w-full border border-gray-300 rounded-md px-4 py-2 text-sm focus:ring-indigo-500 focus:border-indigo-500"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-indigo-600 text-white py-2.5 rounded-md font-bold hover:bg-indigo-700 shadow-sm transition-all text-sm"
                    >
                        Sign In
                    </button>

                    <div className="mt-6 text-center text-sm">
                        <span className="text-gray-500">Don't have an account? </span>
                        <Link to="/register" className="text-indigo-600 font-bold hover:underline">Register here</Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Login;
