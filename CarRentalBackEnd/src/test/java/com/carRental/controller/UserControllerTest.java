package com.carRental.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.carRental.dto.UserDTO;
import com.carRental.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void uploadLicenseImage_ShouldReturnSuccess_WhenUploadIsSuccessful() throws Exception {
        // Arrange
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "license.jpg", "image/jpeg", "content".getBytes());
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setDrivingLicenceImage("content".getBytes());

        when(userService.uploadLicenseImage(eq(userId), any())).thenReturn(userDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/users/{id}/license-image", userId)
                .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("License image uploaded successfully"));
    }

    @Test
    void uploadLicenseImage_ShouldReturnError_WhenUploadFails() throws Exception {
        // Arrange
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "license.jpg", "image/jpeg", "content".getBytes());

        when(userService.uploadLicenseImage(eq(userId), any())).thenThrow(new RuntimeException("Upload failed"));

        // Act & Assert
        mockMvc.perform(multipart("/api/users/{id}/license-image", userId)
                .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Upload failed"));
    }
}
