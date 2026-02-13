import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
    const { user, logout } = useAuth(); // Hook to get current user state
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login'); // Redirect to login page after signing out
    };

    return (
        <nav className="bg-gray-800 text-white p-4 shadow-md sticky top-0 z-50">
            <div className="container mx-auto flex justify-between items-center">
                <Link to="/" className="text-2xl font-bold italic tracking-wider">
                    Quick Drive
                </Link>

                <div className="space-x-6 flex items-center">
                    <Link to="/" className="hover:text-indigo-400 transition-colors">Home</Link>
                    <Link to="/cars" className="hover:text-indigo-400 transition-colors">Cars</Link>

                    {/* Conditional Rendering: Show specific links if user is logged in */}
                    {user ? (
                        <>
                            <Link to="/my-bookings" className="hover:text-indigo-400 transition-colors">My Bookings</Link>

                            {/* Only show Admin Dashboard link if the user is an ADMIN */}
                            {user.role === 'ADMIN' && (
                                <Link to="/admin/dashboard" className="hover:text-indigo-400 transition-colors bg-indigo-600 px-3 py-1 rounded">Admin</Link>
                            )}

                            <div className="flex items-center space-x-4 border-l pl-4 border-gray-600">
                                <Link to="/profile" className="text-sm font-medium text-gray-300 hover:text-white transition-colors">{user.name}</Link>
                                <button
                                    onClick={handleLogout}
                                    className="bg-red-600 hover:bg-red-700 px-4 py-1.5 rounded transition-colors text-sm font-bold"
                                >
                                    Logout
                                </button>
                            </div>
                        </>
                    ) : (
                        // If not logged in, show Login/Signup buttons
                        <div className="space-x-4">
                            <Link to="/login" className="hover:text-indigo-400 font-medium">Login</Link>
                            <Link to="/register" className="bg-indigo-600 hover:bg-indigo-700 px-5 py-2 rounded-md font-bold transition-all">Sign Up</Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
