package com.carRental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import com.carRental.dto.UserDTO;
import com.carRental.entity.User;
import com.carRental.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void uploadLicenseImage_ShouldUploadImageSuccessfully() throws Exception {
        // Arrange
        Long userId = 1L;
        String fileName = "license.jpg";
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", fileName, "image/jpeg", content);

        User user = new User();
        user.setUserId(userId);

        User savedUser = new User();
        savedUser.setUserId(userId);
        savedUser.setDrivingLicenceImage(content);

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setDrivingLicenceImage(content);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.uploadLicenseImage(userId, file);

        // Assert
        assertNotNull(result);
        assertArrayEquals(content, result.getDrivingLicenceImage());
        verify(userRepository).save(user);
    }
}
