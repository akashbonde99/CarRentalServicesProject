package com.carRental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;
import com.carRental.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(
            @RequestBody RegisterRequestDTO registerRequestDTO) {
        UserDTO registeredUser = authService.register(registerRequestDTO);
        return ResponseEntity
                .ok(new ApiResponse<>("User registered successfully", true, registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<com.carRental.dto.AuthResponseDTO>> login(
            @RequestBody com.carRental.dto.LoginRequestDTO loginRequestDTO) {
        com.carRental.dto.AuthResponseDTO authResponse = authService.login(loginRequestDTO);
        return ResponseEntity.ok(new ApiResponse<>("Login successful", true, authResponse));
    }
}
