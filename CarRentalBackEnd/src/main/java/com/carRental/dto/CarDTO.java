package com.carRental.dto;

import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDTO {

    private Long carId;
    private byte[] image;
    private String brand;
    private String model;
    private String registrationNumber;
    private String city;
    private String pickupAddress;
    private String description;
    private Double pricePerDay;
    private Integer seatingCapacity;
    private FuelType fuelType;
    private CarType carType;
    private CarStatus status; // AVAILABLE / BOOKED
}
