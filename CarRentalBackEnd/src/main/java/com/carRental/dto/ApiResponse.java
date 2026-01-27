package com.carRental.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private String message;
    private boolean success;
    private T data;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message) {
        return ApiResponse.<T>builder()
                .message(message)
                .success(false)
                .build();
    }
}
