package com.carRental.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carRental.dto.RegisterRequestDTO;
import com.carRental.dto.UserDTO;
import com.carRental.entity.Role;
import com.carRental.entity.User;
import com.carRental.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Autowired
    private com.carRental.security.JwtUtils jwtUtils;

    @Override
    public UserDTO register(RegisterRequestDTO registerRequestDTO) {
        User user;
        if (registerRequestDTO.getRole() == Role.CUSTOMER) {
            user = modelMapper.map(registerRequestDTO, com.carRental.entity.Customer.class);
        } else if (registerRequestDTO.getRole() == Role.ADMIN) {
            user = modelMapper.map(registerRequestDTO, com.carRental.entity.Admin.class);
        } else {
            user = modelMapper.map(registerRequestDTO, User.class);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public com.carRental.dto.AuthResponseDTO login(com.carRental.dto.LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()));
        var user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtils.generateToken(new com.carRental.security.CustomUserDetails(user));
        return new com.carRental.dto.AuthResponseDTO(token, modelMapper.map(user, UserDTO.class));
    }
}
