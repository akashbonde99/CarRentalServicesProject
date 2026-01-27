package com.carRental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.CarDTO;
import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;
import com.carRental.service.CarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

        private final CarService carService;

        /* ================= ADD CAR (ADMIN) ================= */

        @PostMapping(consumes = { "multipart/form-data" })
        public ResponseEntity<ApiResponse<CarDTO>> addCar(
                        @ModelAttribute CarDTO carDTO,
                        @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile)
                        throws java.io.IOException {

                if (imageFile != null && !imageFile.isEmpty()) {
                        carDTO.setImage(imageFile.getBytes());
                }

                CarDTO createdCar = carService.addCar(carDTO);

                return ResponseEntity.ok(
                                new ApiResponse<>("Car added successfully", true, createdCar));
        }

        /* ================= GET ALL CARS ================= */

        @GetMapping
        public ResponseEntity<ApiResponse<List<CarDTO>>> getAllCars() {

                List<CarDTO> cars = carService.getAllCars();

                return ResponseEntity.ok(
                                new ApiResponse<>("All cars retrieved", true, cars));
        }

        /* ================= GET AVAILABLE CARS ================= */

        @GetMapping("/available")
        public ResponseEntity<ApiResponse<List<CarDTO>>> getAvailableCars() {

                List<CarDTO> cars = carService.getCarsByStatus(CarStatus.AVAILABLE);

                return ResponseEntity.ok(
                                new ApiResponse<>("Available cars retrieved", true, cars));
        }

        /* ================= GET UNIQUE CITIES ================= */

        @GetMapping("/cities")
        public ResponseEntity<ApiResponse<List<String>>> getUniqueCities() {
                List<String> cities = carService.getAllCars()
                                .stream()
                                .map(CarDTO::getCity)
                                .distinct()
                                .collect(java.util.stream.Collectors.toList());

                return ResponseEntity.ok(
                                new ApiResponse<>("Unique cities retrieved", true, cities));
        }

        /* ================= GET CAR BY ID ================= */

        @GetMapping("/{carId}")
        public ResponseEntity<ApiResponse<CarDTO>> getCarById(
                        @PathVariable Long carId) {

                CarDTO car = carService.getCarById(carId);

                return ResponseEntity.ok(
                                new ApiResponse<>("Car found", true, car));
        }

        /* ================= SEARCH ================= */

        @GetMapping("/search/fuel/{fuelType}")
        public ResponseEntity<ApiResponse<List<CarDTO>>> searchByFuelType(
                        @PathVariable FuelType fuelType) {

                List<CarDTO> cars = carService.searchCarsByFuelType(fuelType);

                return ResponseEntity.ok(
                                new ApiResponse<>("Cars found", true, cars));
        }

        @GetMapping("/search/type/{carType}")
        public ResponseEntity<ApiResponse<List<CarDTO>>> searchByCarType(
                        @PathVariable CarType carType) {

                List<CarDTO> cars = carService.searchCarsByCarType(carType);

                return ResponseEntity.ok(
                                new ApiResponse<>("Cars found", true, cars));
        }
}
