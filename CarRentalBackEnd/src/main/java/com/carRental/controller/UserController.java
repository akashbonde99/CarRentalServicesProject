package com.carRental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.UserDTO;
import com.carRental.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/{id}/license-image")
    public ResponseEntity<ApiResponse<UserDTO>> uploadLicenseImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            UserDTO updatedUser = userService.uploadLicenseImage(id, file);
            return ResponseEntity.ok(new ApiResponse<>("License image uploaded successfully", true, updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
}
