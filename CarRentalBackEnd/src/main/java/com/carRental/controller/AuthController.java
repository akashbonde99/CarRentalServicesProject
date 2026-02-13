package com.carRental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;
import com.carRental.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // This endpoint handles new user registration.
    // It takes user details (like name, email, password) from the request body,
    // verifies them, and if everything represents a valid new user, it saves them
    // to the database.
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserDTO registeredUser = authService.register(registerRequestDTO);
        return ResponseEntity
                .ok(new ApiResponse<>("User registered successfully", true, registeredUser));
    }

    // This is the login endpoint.
    // When a user tries to sign in, we check their email and password.
    // If they match our records, we generate a special token (JWT) for them so they
    // can stay logged in.
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.carRental.dto.AuthResponseDTO>> login(
            @RequestBody com.carRental.dto.LoginRequestDTO loginRequestDTO) {
        com.carRental.dto.AuthResponseDTO authResponse = authService.login(loginRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", true, authResponse));
    }

    // Handles requests when a user forgets their password.
    // It sends an OTP (One Time Password) to their registered email address.
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody com.carRental.dto.ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(new ApiResponse<>("OTP sent to your email if registered", true, null));
    }

    // Verifies the OTP entered by the user.
    // If the OTP matches what we sent to their email, we allow them to proceed to
    // reset their password.
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @RequestBody com.carRental.dto.VerifyOtpRequest request) {
        boolean isValid = authService.verifyOtp(request.getEmail(), request.getOtp());
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse<>("OTP verified successfully", true, null));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Invalid or expired OTP", false, null));
        }
    }

    // Finally, this endpoint updates the user's password to the new one they chose.
    // This only happens after the OTP has been verified.
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody com.carRental.dto.ResetPasswordRequest request) {
        try {
            authService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse<>("Password reset successful", true, null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
}
