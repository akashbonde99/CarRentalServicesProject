package com.carRental.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carRental.dto.UserDTO;
import com.carRental.entity.Role;
import com.carRental.entity.User;
import com.carRental.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.carRental.dto.ApiResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * List all admin users whose accounts are not yet active.
     * These represent admin registration requests waiting for approval.
     */
    @GetMapping("/pending-admins")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getPendingAdmins() {
        List<User> pendingAdmins = userRepository.findByRoleAndActiveFalse(Role.ADMIN);
        List<UserDTO> pendingAdminDtos = pendingAdmins.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();
        return ResponseEntity.ok(
                new ApiResponse<>("Pending admins fetched successfully", true, pendingAdminDtos));
    }

    /**
     * Approve an admin registration request by activating the admin account.
     */
    @PostMapping("/approve-admin/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> approveAdmin(@PathVariable Long id) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>("User is not an admin", false, null));
        }

        admin.setActive(true);
        User saved = userRepository.save(admin);
        UserDTO dto = modelMapper.map(saved, UserDTO.class);
        return ResponseEntity.ok(
                new ApiResponse<>("Admin approved successfully", true, dto));
    }
}
