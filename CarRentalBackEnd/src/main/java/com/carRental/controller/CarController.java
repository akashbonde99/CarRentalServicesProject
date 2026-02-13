package com.carRental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carRental.dto.ApiResponse;
import com.carRental.dto.CarDTO;
import com.carRental.service.CarService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

        private final CarService carService;

        /* ================= ADD CAR (ADMIN) ================= */

        // Admin Only: Adds a new car to the fleet.
        // It handles multiparts because we need to upload the car's image along with
        // its details.
        @PostMapping(consumes = { "multipart/form-data" })
        public ResponseEntity<ApiResponse<CarDTO>> addCar(
                        @ModelAttribute CarDTO carDTO,
                        @RequestParam(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile)
                        throws java.io.IOException {

                if (imageFile != null && !imageFile.isEmpty()) {
                        carDTO.setImage(imageFile.getBytes()); // Convert image to bytes for DB storage
                }

                CarDTO createdCar = carService.addCar(carDTO);

                return ResponseEntity.ok(
                                new ApiResponse<>("Car added successfully", true, createdCar));
        }

        /* ================= GET ALL CARS ================= */

        // Public: Lists all vehicles in our system.
        // Used for the main browsing page.
        @GetMapping
        public ResponseEntity<ApiResponse<List<CarDTO>>> getAllCars() {

                List<CarDTO> cars = carService.getAllCars();

                return ResponseEntity.ok(
                                new ApiResponse<>("All cars retrieved", true, cars));
        }

        /* ================= GET UNIQUE CITIES ================= */

        // Helper endpoint for the Search Bar.
        // It looks at all our cars and finds all the unique cities they are located in.
        // This populates the "Location" dropdown on the frontend so users only pick
        // valid cities.
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

        // Fetches details for a single specific car.
        // Used when a user clicks on a car card to see more info or book it.
        @GetMapping("/{carId}")
        public ResponseEntity<ApiResponse<CarDTO>> getCarById(
                        @PathVariable Long carId) {

                CarDTO car = carService.getCarById(carId);

                return ResponseEntity.ok(
                                new ApiResponse<>("Car found", true, car));
        }

        /* ================= SEARCH ================= */

        // The main search engine.
        // Users can filter by City, Pickup Date, and Drop Date.
        // If no dates are provided, it just filters by location (simple text match).
        // If dates ARE provided, it checks availability logic (is the car free during
        // these dates?).
        @GetMapping("/search")
        public ResponseEntity<ApiResponse<List<CarDTO>>> searchCars(
                        @RequestParam(required = false) String location,
                        @RequestParam(required = false) java.time.LocalDate pickupDate,
                        @RequestParam(required = false) java.time.LocalDate dropDate) {

                if (pickupDate == null || dropDate == null) {
                        // Simple location filter if no dates selected
                        if (location != null && !location.trim().isEmpty()) {
                                List<CarDTO> allCars = carService.getAllCars();
                                String locLower = location.toLowerCase();
                                allCars = allCars.stream()
                                                .filter(c -> (c.getCity() != null
                                                                && c.getCity().toLowerCase().contains(locLower)) ||
                                                                (c.getPickupAddress() != null
                                                                                && c.getPickupAddress().toLowerCase()
                                                                                                .contains(locLower)))
                                                .collect(java.util.stream.Collectors.toList());
                                return ResponseEntity.ok(new ApiResponse<>("Cars retrieved", true, allCars));
                        }
                        return getAllCars();
                }

                // Advanced filter: Checking effective availability in the database
                List<CarDTO> cars = carService.searchAvailableCars(location, pickupDate, dropDate);
                return ResponseEntity.ok(new ApiResponse<>("Available cars found", true, cars));
        }

        /* ================= DELETE CAR (ADMIN) ================= */

        // Admin Only: Removes a car from the system.
        @DeleteMapping("/{carId}")
        public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long carId) {
                try {
                        System.out.println("Processing delete request for Car ID: " + carId);
                        carService.deleteCar(carId);
                        return ResponseEntity.ok(
                                        new ApiResponse<>("Car deleted successfully", true, null));
                } catch (Exception e) {
                        System.err.println("Error deleting car: " + e.getMessage());
                        return ResponseEntity.status(500).body(
                                        new ApiResponse<>("Error: " + e.getMessage(), false, null));
                }
        }
}
