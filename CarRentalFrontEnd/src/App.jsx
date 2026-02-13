import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import Home from './pages/Home';
import { AuthProvider } from './context/AuthContext';

import Login from './pages/Login';
import Register from './pages/Register';
import AdminDashboard from './pages/AdminDashboard';
import BookCar from './pages/BookCar';
import MyBookings from './pages/MyBookings';
import Cars from './pages/Cars';

import Checkout from './pages/Checkout';
import Profile from './pages/Profile';
import BookingDetails from './pages/BookingDetails';
import ForgotPassword from './pages/ForgotPassword';

function App() {
  return (
    // AuthProvider wraps the whole app so that every page knows if the user is logged in or not.
    <AuthProvider>
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        
        {/* These are our pages and their corresponding URLs */}
        <Routes>
          {/* Public Pages */}
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/cars" element={<Cars />} />
          
          {/* Protected / User Specific Pages */}
          <Route path="/book/:carId" element={<BookCar />} />
          <Route path="/booking/:bookingId" element={<BookingDetails />} />
          <Route path="/my-bookings" element={<MyBookings />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/checkout/:bookingId" element={<Checkout />} />
          
          {/* Admin Only Page */}
          <Route path="/admin/dashboard" element={<AdminDashboard />} />
        </Routes>
        
        <Footer />
      </div>
    </AuthProvider>
  );
}

export default App;
