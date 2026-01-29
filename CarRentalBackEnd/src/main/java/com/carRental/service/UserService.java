package com.carRental.service;

import org.springframework.web.multipart.MultipartFile;
import com.carRental.dto.UserDTO;

public interface UserService {
    UserDTO uploadLicenseImage(Long userId, MultipartFile file) throws Exception;
}
