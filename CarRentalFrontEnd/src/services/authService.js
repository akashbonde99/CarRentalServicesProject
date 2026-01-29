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

/**
 * Fetch all admin users whose accounts are still pending approval.
 */
export const getPendingAdmins = async () => {
    try {
        const response = await api.get('/admin/pending-admins');
        return response.data;
    } catch (error) {
        console.error('AuthService.getPendingAdmins error:', error);
        throw error.response?.data || error.message;
    }
};

/**
 * Approve a specific admin by ID.
 */
export const approveAdmin = async (adminId) => {
    try {
        const response = await api.post(`/admin/approve-admin/${adminId}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const uploadLicense = async (userId, file) => {
    try {
        const formData = new FormData();
        formData.append('file', file);
        const response = await api.post(`/users/${userId}/license-image`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        return response.data;
    } catch (error) {
        console.error('AuthService.uploadLicense error:', error);
        throw error.response?.data || error.message;
    }
};
