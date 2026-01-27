import api from './api';

/**
 * Modernized Auth Service
 * High-reliability data flow for authentication.
 */

export const loginUser = async ({ email, password }) => {
    try {
        // Explicitly structuring the payload to ensure Jackson can deserialize.
        // Spring @RequestBody expects an object matching LoginRequestDTO.
        const payload = {
            email: String(email).trim(),
            password: String(password)
        };

        const response = await api.post('/auth/login', payload);
        return response.data;
    } catch (error) {
        console.error('AuthService.loginUser error:', error);
        throw error.response?.data || error.message;
    }
};

export const registerUser = async (userData) => {
    try {
        // Cleaning userData before transmission
        const payload = {
            ...userData,
            email: userData.email?.trim(),
            name: userData.name?.trim()
        };

        const response = await api.post('/auth/register', payload);
        return response.data;
    } catch (error) {
        console.error('AuthService.registerUser error:', error);
        throw error.response?.data || error.message;
    }
};
