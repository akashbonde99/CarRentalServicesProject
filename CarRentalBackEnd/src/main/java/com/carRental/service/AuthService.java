package com.carRental.service;

import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;

public interface AuthService {
    UserDTO register(RegisterRequestDTO registerRequestDTO);

    com.carRental.dto.AuthResponseDTO login(com.carRental.dto.LoginRequestDTO loginRequestDTO);
}
