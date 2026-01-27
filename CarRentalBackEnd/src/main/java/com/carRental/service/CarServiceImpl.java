package com.carRental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.carRental.dto.CarDTO;
import com.carRental.entity.Car;
import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;
import com.carRental.repository.CarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    /* ================= ADD CAR (ADMIN) ================= */

    @Override
    public CarDTO addCar(CarDTO carDTO) {

        Car car = Car.builder()
                .brand(carDTO.getBrand())
                .model(carDTO.getModel())
                .registrationNumber(carDTO.getRegistrationNumber())
                .city(carDTO.getCity())
                .pickupAddress(carDTO.getPickupAddress())
                .description(carDTO.getDescription())
                .pricePerDay(carDTO.getPricePerDay())
                .fuelType(carDTO.getFuelType())
                .carType(carDTO.getCarType())
                .image(carDTO.getImage())
                .status(CarStatus.AVAILABLE) // default
                .build();

        Car savedCar = carRepository.save(car);

        return mapToDTO(savedCar);
    }

    /* ================= DELETE CAR (ADMIN) ================= */

    @Override
    public void deleteCar(Long carId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        carRepository.delete(car);
    }

    /* ================= GET ALL CARS ================= */

    @Override
    public List<CarDTO> getAllCars() {

        return carRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ================= GET CAR BY ID ================= */

    @Override
    public CarDTO getCarById(Long carId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        return mapToDTO(car);
    }

    /* ================= GET CARS BY STATUS ================= */

    @Override
    public List<CarDTO> getCarsByStatus(CarStatus status) {

        return carRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ================= SEARCH ================= */

    @Override
    public List<CarDTO> searchCarsByFuelType(FuelType fuelType) {

        return carRepository.findByFuelType(fuelType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CarDTO> searchCarsByCarType(CarType carType) {

        return carRepository.findByCarType(carType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ================= MAPPER ================= */

    private CarDTO mapToDTO(Car car) {

        return CarDTO.builder()
                .carId(car.getCarId())
                .brand(car.getBrand())
                .model(car.getModel())
                .registrationNumber(car.getRegistrationNumber())
                .city(car.getCity())
                .pickupAddress(car.getPickupAddress())
                .description(car.getDescription())
                .pricePerDay(car.getPricePerDay())
                .fuelType(car.getFuelType())
                .carType(car.getCarType())
                .image(car.getImage())
                .status(car.getStatus())
                .build();
    }
}
