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

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    // Handles the sign-up process.
    // 1. Checks if the email is already in use.
    // 2. Encrypts the password so we don't store it in plain text (security
    // first!).
    // 3. Assigns the role (Admin or Customer). Admins need approval, so they start
    // as inactive.
    // 4. Saves the new user to our database.
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

        // Set account activation based on role.
        // - Customers: active immediately.
        // - Admins: start as inactive and require approval by an existing admin.
        if (user.getRole() == Role.ADMIN) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDTO.class);
    }

    // Handles the login process.
    // 1. We ask Spring Security to authenticate the email and password.
    // 2. If successful, we fetch the user's details.
    // 3. We generate a JWT token (like a digital ID card) that they can use for
    // future requests.
    @Override
    public com.carRental.dto.AuthResponseDTO login(com.carRental.dto.LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        loginRequestDTO.getEmail(),
                        loginRequestDTO.getPassword()));
        var user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure user is active before generating token
        if (!user.isActive()) {
            throw new RuntimeException("Account is inactive. Please contact admin.");
        }

        String token = jwtUtils.generateToken(new com.carRental.security.CustomUserDetails(user));
        return new com.carRental.dto.AuthResponseDTO(token, modelMapper.map(user, UserDTO.class));
    }

    // --- Forgotten Password Flow ---

    // Step 1: User gives us their email.
    // We generate a random OTP and email it to them using our OTP Service.
    @Override
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String otp = otpService.generateOtp(email);
            emailService.sendOtpEmail(email, otp);
        });
        // We don't throw error if not found to prevent email enumeration attacks.
    }

    // Step 2: User enters the OTP they received.
    // We check if it matches what we saved/generated.
    @Override
    public boolean verifyOtp(String email, String otp) {
        return otpService.verifyOtp(email, otp);
    }

    // Step 3: User sets a new password.
    // We check the OTP one last time for security, then update the password
    // (encrypted, of course) and clear the OTP.
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        if (!otpService.isValidForReset(email, otp)) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Success -> Clear OTP so it can't be reused.
        otpService.clearOtp(email);
    }
}
