package com.carRental.dto;

import com.carRental.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {

    private String name;
    private String email;
    private String password;
    private String drivingLicence; // optional
    private Role role;
}
