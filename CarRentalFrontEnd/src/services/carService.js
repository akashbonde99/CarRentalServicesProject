import api from './api';

export const getAllCars = async () => {
    try {
        const response = await api.get('/cars');
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const getAvailableCars = async () => {
    try {
        const response = await api.get('/cars/available');
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const searchCars = async (searchParams) => {
    try {
        const response = await api.get('/cars/search', { params: searchParams });
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

// Mock function to get car image
export const getCarImage = (brand) => {
    // Return a relevant placeholder image based on brand or default
    const images = {
        'Toyota': 'https://images.unsplash.com/photo-1590362891991-f776e747a588?auto=format&fit=crop&w=400&q=80',
        'Honda': 'https://images.unsplash.com/photo-1618843479313-40f8afb4b4d8?auto=format&fit=crop&w=400&q=80',
        'BMW': 'https://images.unsplash.com/photo-1555215695-3004980adade?auto=format&fit=crop&w=400&q=80',
        'Mercedes': 'https://images.unsplash.com/photo-1617788138017-80ad40651399?auto=format&fit=crop&w=400&q=80',
        'Tesla': 'https://images.unsplash.com/photo-1536700503339-1e4b06520771?auto=format&fit=crop&w=400&q=80',
        'Ford': 'https://images.unsplash.com/photo-1551830524-10c123299c75?auto=format&fit=crop&w=400&q=80',
        'default': 'https://images.unsplash.com/photo-1494976388531-d1058494cdd8?auto=format&fit=crop&w=400&q=80'
    };
    return images[brand] || images['default'];
};

export const addCar = async (carData) => {
    try {
        // carData should be FormData object
        const response = await api.post('/cars', carData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};

export const deleteCar = async (carId) => {
    try {
        const response = await api.delete(`/admin/cars/${carId}`);
        return response.data;
    } catch (error) {
        throw error.response?.data || error.message;
    }
};
