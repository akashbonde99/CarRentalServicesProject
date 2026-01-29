package com.carRental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.carRental.dto.CarDTO;
import com.carRental.entity.Car;
import com.carRental.entity.CarStatus;
import com.carRental.repository.CarRepository;

@ExtendWith(MockitoExtension.class)
public class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void addCar_ShouldSaveAndReturnCarDTO() {
        // Arrange
        CarDTO carDTO = CarDTO.builder()
                .brand("Brand")
                .model("Model")
                .registrationNumber("REG123")
                .pricePerDay(100.0)
                .seatingCapacity(5)
                .build();

        Car car = Car.builder()
                .carId(1L)
                .brand("Brand")
                .model("Model")
                .registrationNumber("REG123")
                .pricePerDay(100.0)
                .seatingCapacity(5)
                .status(CarStatus.AVAILABLE)
                .build();

        when(carRepository.save(any(Car.class))).thenReturn(car);

        // Act
        CarDTO result = carService.addCar(carDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Brand", result.getBrand());
        assertEquals("REG123", result.getRegistrationNumber());
        assertEquals(5, result.getSeatingCapacity());
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void getCarById_ShouldReturnCar_WhenFound() {
        // Arrange
        Long carId = 1L;
        Car car = Car.builder()
                .carId(carId)
                .brand("Brand")
                .seatingCapacity(5)
                .status(CarStatus.AVAILABLE)
                .build();

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        CarDTO result = carService.getCarById(carId);

        // Assert
        assertNotNull(result);
        assertEquals(carId, result.getCarId());
    }

    @Test
    void deleteCar_ShouldCallDelete_WhenCarExists() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();
        car.setCarId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        carService.deleteCar(carId);

        // Assert
        verify(carRepository).delete(car);
    }
}
