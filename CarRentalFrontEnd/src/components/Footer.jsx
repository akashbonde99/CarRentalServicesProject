import React from 'react';
import { FaFacebook, FaTwitter, FaInstagram, FaLinkedin } from 'react-icons/fa';

const Footer = () => {
    return (
        <footer className="bg-gray-900 text-white py-12 mt-auto">
            <div className="container mx-auto px-6">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-12">
                    <div className="space-y-4">
                        <h3 className="text-2xl font-bold italic">RentCars</h3>
                        <p className="text-gray-400 text-sm leading-relaxed">
                            Premium car rental service providing quality vehicles for your journey. Reliable, fast, and easy.
                        </p>
                    </div>

                    <div>
                        <h4 className="text-lg font-bold mb-6">Quick Links</h4>
                        <ul className="space-y-3 text-gray-400 text-sm">
                            <li><a href="/" className="hover:text-white transition-colors">Home</a></li>
                            <li><a href="/cars" className="hover:text-white transition-colors">Our Fleet</a></li>
                            <li><a href="/about" className="hover:text-white transition-colors">About Us</a></li>
                            <li><a href="/contact" className="hover:text-white transition-colors">Contact</a></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="text-lg font-bold mb-6">Support</h4>
                        <ul className="space-y-3 text-gray-400 text-sm">
                            <li><a href="#" className="hover:text-white transition-colors">Help Center</a></li>
                            <li><a href="#" className="hover:text-white transition-colors">Safety Information</a></li>
                            <li><a href="#" className="hover:text-white transition-colors">Terms of Service</a></li>
                            <li><a href="#" className="hover:text-white transition-colors">Privacy Policy</a></li>
                        </ul>
                    </div>

                    <div>
                        <h4 className="text-lg font-bold mb-6">Connect With Us</h4>
                        <div className="flex space-x-5">
                            <a href="#" className="text-gray-400 hover:text-white transition-colors"><FaFacebook size={24} /></a>
                            <a href="#" className="text-gray-400 hover:text-white transition-colors"><FaTwitter size={24} /></a>
                            <a href="#" className="text-gray-400 hover:text-white transition-colors"><FaInstagram size={24} /></a>
                            <a href="#" className="text-gray-400 hover:text-white transition-colors"><FaLinkedin size={24} /></a>
                        </div>
                    </div>
                </div>

                <div className="border-t border-gray-800 mt-12 pt-8 text-center text-gray-500 text-sm">
                    <p>&copy; {new Date().getFullYear()} RentCars. All rights reserved.</p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
