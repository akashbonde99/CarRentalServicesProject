package com.carRental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carRental.service.CarService;
import com.carRental.dto.ApiResponse;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private CarService carService;

    @DeleteMapping("/cars/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long id) {
        try {
            carService.deleteCar(id);
            return ResponseEntity.ok(new ApiResponse<>("Car deleted successfully", true, null));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(e.getMessage(), false, null));
        }
    }
}
