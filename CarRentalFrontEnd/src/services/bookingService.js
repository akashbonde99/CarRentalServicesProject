import api from './api';

export const createBooking = async (bookingData) => {
    try {
        const response = await api.post('/bookings', bookingData);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getMyBookings = async () => {
    try {
        const response = await api.get('/bookings/my-bookings');
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const cancelBooking = async (bookingId) => {
    try {
        const response = await api.put(`/bookings/${bookingId}/cancel`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getAllBookings = async () => {
    try {
        const response = await api.get('/bookings');
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const updateBookingStatus = async (bookingId, status) => {
    try {
        const response = await api.put(`/bookings/${bookingId}/status/${status}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};
