package com.carRental.service;

import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;

public interface AuthService {
    // Registers a new user (Customer or Admin)
    UserDTO register(RegisterRequestDTO registerRequestDTO);

    // Logs in a user and returns a JWT token
    com.carRental.dto.AuthResponseDTO login(com.carRental.dto.LoginRequestDTO loginRequestDTO);

    // Intiates password reset flow
    void forgotPassword(String email);

    // Checks if the OTP is valid
    boolean verifyOtp(String email, String otp);

    // Updates the password
    void resetPassword(String email, String otp, String newPassword);
}
