package com.carRental.service;

import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;

public interface AuthService {
    UserDTO register(RegisterRequestDTO registerRequestDTO);

    com.carRental.dto.AuthResponseDTO login(com.carRental.dto.LoginRequestDTO loginRequestDTO);

    void forgotPassword(String email);

    boolean verifyOtp(String email, String otp);

    void resetPassword(String email, String otp, String newPassword);
}
