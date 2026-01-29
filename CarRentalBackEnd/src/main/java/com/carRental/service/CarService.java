package com.carRental.service;

import java.util.List;

import com.carRental.dto.CarDTO;
import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;

public interface CarService {
    CarDTO addCar(CarDTO carDTO);

    List<CarDTO> getAllCars();

    List<CarDTO> getCarsByStatus(CarStatus status);

    CarDTO getCarById(Long id);

    List<CarDTO> searchCarsByFuelType(FuelType fuelType);

    List<CarDTO> searchCarsByCarType(CarType carType);

    List<CarDTO> searchAvailableCars(String city, java.time.LocalDate pickupDate, java.time.LocalDate dropDate);

    void deleteCar(Long id);
}
