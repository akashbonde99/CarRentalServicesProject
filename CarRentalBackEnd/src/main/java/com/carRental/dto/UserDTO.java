package com.carRental.dto;

import com.carRental.entity.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private String drivingLicence;
    private byte[] drivingLicenceImage;
}
