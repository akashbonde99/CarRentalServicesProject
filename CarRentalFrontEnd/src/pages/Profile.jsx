import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { uploadLicense } from '../services/authService';

const Profile = () => {
    const { user, login } = useAuth(); // login is used to update user in context if needed
    const [file, setFile] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [message, setMessage] = useState('');
    const [preview, setPreview] = useState(user?.drivingLicenceImage ? `data:image/jpeg;base64,${user.drivingLicenceImage}` : null);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        setFile(selectedFile);
        if (selectedFile) {
            setPreview(URL.createObjectURL(selectedFile));
        }
    };

    const handleUpload = async () => {
        if (!file) {
            setMessage("Please select a file first.");
            return;
        }

        setUploading(true);
        setMessage('');

        try {
            const response = await uploadLicense(user.userId, file);
            if (response.success) {
                setMessage("License uploaded successfully!");
                // Update local user state and context
                // Ideally backend returns the full updated UserDTO in response.data
                if (response.data) {
                    // Update context
                    login(response.data, localStorage.getItem('token'));
                }
            } else {
                setMessage(response.message || "Upload failed.");
            }
        } catch (error) {
            setMessage("An error occurred during upload.");
        } finally {
            setUploading(false);
        }
    };

    if (!user) return <div className="p-10 text-center">Please login to view profile.</div>;

    return (
        <div className="container mx-auto px-6 py-12">
            <h2 className="text-3xl font-bold mb-8">My Profile</h2>

            <div className="bg-white rounded-lg shadow-md p-8 border border-gray-200 max-w-2xl mx-auto">
                <div className="flex items-center space-x-6 mb-8">
                    <div className="w-20 h-20 bg-indigo-100 rounded-full flex items-center justify-center text-2xl font-bold text-indigo-600">
                        {user.name.charAt(0)}
                    </div>
                    <div>
                        <h3 className="text-xl font-bold">{user.name}</h3>
                        <p className="text-gray-500">{user.email}</p>
                        <span className="inline-block mt-2 bg-gray-100 text-gray-800 text-xs px-2 py-1 rounded font-semibold uppercase">
                            {user.role}
                        </span>
                    </div>
                </div>

                <div className="border-t border-gray-100 pt-8">
                    <h4 className="text-lg font-bold mb-4">Driver's License</h4>

                    <div className="mb-6">
                        <label className="block text-sm font-medium text-gray-700 mb-2">
                            Upload License Image
                        </label>
                        <div className="flex items-center space-x-4">
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleFileChange}
                                className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-indigo-50 file:text-indigo-700 hover:file:bg-indigo-100"
                            />
                            <button
                                onClick={handleUpload}
                                disabled={uploading || !file}
                                className={`px-4 py-2 rounded-md font-bold text-white transition-colors ${uploading || !file ? 'bg-gray-400 cursor-not-allowed' : 'bg-indigo-600 hover:bg-indigo-700'}`}
                            >
                                {uploading ? 'Uploading...' : 'Upload'}
                            </button>
                        </div>
                        {message && (
                            <p className={`mt-2 text-sm ${message.includes('success') ? 'text-green-600' : 'text-red-600'}`}>
                                {message}
                            </p>
                        )}
                    </div>

                    {preview ? (
                        <div className="mt-4">
                            <p className="text-sm text-gray-500 mb-2">License Preview:</p>
                            <img src={preview} alt="License Preview" className="max-w-full h-auto rounded-lg border border-gray-200 shadow-sm" style={{ maxHeight: '300px' }} />
                        </div>
                    ) : (
                        <div className="mt-4 p-6 bg-gray-50 rounded-lg border border-gray-200 text-center text-gray-500 text-sm">
                            No license image uploaded yet.
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Profile;
