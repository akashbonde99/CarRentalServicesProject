package com.carRental.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carRental.dto.CarDTO;
import com.carRental.entity.Car;
import com.carRental.entity.CarLocation;
import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;
import com.carRental.repository.CarLocationRepository;
import com.carRental.repository.CarRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarLocationRepository locationRepository;

    /* ================= ADD CAR (ADMIN) ================= */

    // Takes a CarDTO (data from frontend), converts it to a Car entity, and saves
    // it.
    // If a Google Maps URL is provided, it also creates and links a CarLocation
    // entity.
    @Override
    public CarDTO addCar(CarDTO carDTO) {

        // 1. Build the Car Entity from the input DTO
        Car car = Car.builder()
                .brand(carDTO.getBrand())
                .model(carDTO.getModel())
                .registrationNumber(carDTO.getRegistrationNumber())
                .city(carDTO.getCity())
                .pickupAddress(carDTO.getPickupAddress())
                .description(carDTO.getDescription())
                .pricePerDay(carDTO.getPricePerDay())
                .seatingCapacity(carDTO.getSeatingCapacity())
                .fuelType(carDTO.getFuelType())
                .carType(carDTO.getCarType())
                .image(carDTO.getImage())
                .status(CarStatus.AVAILABLE) // New cars are always available by default
                .build();

        // 2. Handle Location (Optional Map URL)
        if (carDTO.getMapUrl() != null && !carDTO.getMapUrl().trim().isEmpty()) {
            CarLocation loc = new CarLocation();
            loc.setCar(car);
            loc.setMapUrl(carDTO.getMapUrl().trim());
            loc.setAddress(carDTO.getPickupAddress());
            loc.setCity(car.getCity());
            loc.setName(car.getBrand() + " " + car.getModel() + " Location");
            car.setLocation(loc);
        }

        // 3. Save to Database
        Car savedCar = carRepository.save(car);

        // 4. Return the saved data as a DTO
        return mapToDTO(savedCar);
    }

    /* ================= DELETE CAR (ADMIN) ================= */

    // Finds a car by ID and deletes it.
    // Throws an error if the car doesn't exist.
    @Override
    public void deleteCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));
        carRepository.delete(car);
    }

    /* ================= GET ALL CARS ================= */

    // Fetches all cars from the database and converts them to DTOs for the
    // frontend.
    @Override
    public List<CarDTO> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(this::mapToDTO) // Convert Entity -> DTO
                .collect(Collectors.toList());
    }

    /* ================= GET CAR BY ID ================= */

    // Retrieves a single car's details. Useful for the "Car Details" page.
    @Override
    public CarDTO getCarById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        return mapToDTO(car);
    }

    /* ================= GET CARS BY STATUS ================= */

    // Helper to find all available cars, or all booked cars.
    @Override
    public List<CarDTO> getCarsByStatus(CarStatus status) {
        return carRepository.findByStatus(status)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ================= SEARCH ================= */

    // Filters cars by Fuel Type (e.g., Petrol, Diesel, Electric).
    @Override
    public List<CarDTO> searchCarsByFuelType(FuelType fuelType) {
        return carRepository.findByFuelType(fuelType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Filters cars by Body Type (e.g., SUV, Sedan).
    @Override
    public List<CarDTO> searchCarsByCarType(CarType carType) {
        return carRepository.findByCarType(carType)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // The Core Search Logic.
    // Finds cars matching the City and checks if they are free during the requested
    // dates.
    // The complex date logic is handled by the custom query in `CarRepository`.
    @Override
    public List<CarDTO> searchAvailableCars(String city, java.time.LocalDate pickupDate, java.time.LocalDate dropDate) {
        return carRepository.findAvailableCars(city, pickupDate, dropDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ================= MAPPER ================= */

    // Converts a Car Entity (Database Object) to a CarDTO (Data Transfer Object).
    // This hides internal DB details and formats data for the API.
    private CarDTO mapToDTO(Car car) {
        // Fetch the Map URL if it exists
        String mapUrl = locationRepository.findByCar(car)
                .map(CarLocation::getMapUrl)
                .orElse(null);

        return CarDTO.builder()
                .carId(car.getCarId())
                .brand(car.getBrand())
                .model(car.getModel())
                .registrationNumber(car.getRegistrationNumber())
                .city(car.getCity())
                .pickupAddress(car.getPickupAddress())
                .description(car.getDescription())
                .pricePerDay(car.getPricePerDay())
                .seatingCapacity(car.getSeatingCapacity())
                .fuelType(car.getFuelType())
                .carType(car.getCarType())
                .image(car.getImage())
                .status(car.getStatus())
                .mapUrl(mapUrl)
                .build();
    }
}
