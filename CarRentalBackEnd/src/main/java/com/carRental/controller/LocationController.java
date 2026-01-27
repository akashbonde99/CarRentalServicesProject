package com.carRental.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carRental.dto.ApiResponse;
import com.carRental.entity.CarLocation;
import com.carRental.service.LocationService;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @PostMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarLocation>> addLocation(@PathVariable Long carId,
            @RequestBody CarLocation location) {
        CarLocation savedLocation = locationService.addLocation(carId, location);
        return ResponseEntity.ok(new ApiResponse<>("Location added", true, savedLocation));
    }

    @PutMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarLocation>> updateLocation(@PathVariable Long carId,
            @RequestBody CarLocation location) {
        CarLocation updatedLocation = locationService.updateLocation(carId, location);
        return ResponseEntity.ok(new ApiResponse<>("Location updated", true, updatedLocation));
    }

    @GetMapping("/{carId}")
    public ResponseEntity<ApiResponse<CarLocation>> getLocation(@PathVariable Long carId) {
        CarLocation location = locationService.getLocationByCarId(carId);
        if (location != null) {
            return ResponseEntity.ok(new ApiResponse<>("Location found", true, location));
        }
        return ResponseEntity.status(404).body(new ApiResponse<>("Location not found", false, null));
    }
}
