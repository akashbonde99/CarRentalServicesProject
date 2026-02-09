package com.carRental.service;

import com.carRental.dto.OtpData;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 3;

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        OtpData otpData = new OtpData(otp, LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES), 0);
        otpStorage.put(email, otpData);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        OtpData otpData = otpStorage.get(email);

        if (otpData == null) {
            return false;
        }

        // Check expiry
        if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
            otpStorage.remove(email);
            return false;
        }

        // Check attempts
        if (otpData.getAttempts() >= MAX_ATTEMPTS) {
            otpStorage.remove(email);
            return false;
        }

        if (otpData.getOtp().equals(otp)) {
            // Keep it for the final reset step, but we will clear it there
            return true;
        } else {
            otpData.setAttempts(otpData.getAttempts() + 1);
            return false;
        }
    }

    public void clearOtp(String email) {
        otpStorage.remove(email);
    }

    public boolean isValidForReset(String email, String otp) {
        OtpData otpData = otpStorage.get(email);
        return otpData != null &&
                otpData.getOtp().equals(otp) &&
                LocalDateTime.now().isBefore(otpData.getExpiryTime()) &&
                otpData.getAttempts() < MAX_ATTEMPTS;
    }
}
