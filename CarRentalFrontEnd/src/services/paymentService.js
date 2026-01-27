import api from './api';

export const processPayment = async (paymentData) => {
    try {
        const response = await api.post('/payments', paymentData);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getPaymentByBookingId = async (bookingId) => {
    try {
        const response = await api.get(`/payments/booking/${bookingId}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};
