package com.carRental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carRental.dto.BookingRequestDTO;
import com.carRental.entity.Car;
import com.carRental.entity.CarStatus;
import com.carRental.entity.User;
import com.carRental.repository.BookingRepository;
import com.carRental.repository.CarRepository;
import com.carRental.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createBooking_ShouldThrowException_WhenPickupCityDoesNotMatch() {
        // Arrange
        Long carId = 1L;
        String userEmail = "test@example.com";

        BookingRequestDTO request = BookingRequestDTO.builder()
                .carId(carId)
                .pickupDate(LocalDate.now().plusDays(1))
                .dropDate(LocalDate.now().plusDays(3))
                .pickupCity("Mumbai")
                .build();

        User user = new User();
        user.setEmail(userEmail);

        Car car = new Car();
        car.setCarId(carId);
        car.setCity("Pune");
        car.setStatus(CarStatus.AVAILABLE);
        car.setPricePerDay(100.0);
        car.setSeatingCapacity(5);

        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(userEmail);
        lenient().when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            bookingService.createBooking(request);
        });

        assertEquals("Pickup city must match the car's location: Pune", exception.getMessage());
    }

    @Test
    void createBooking_ShouldSucceed_WhenPickupCityMatches() {
        // Arrange
        Long carId = 1L;
        String userEmail = "test@example.com";

        BookingRequestDTO request = BookingRequestDTO.builder()
                .carId(carId)
                .pickupDate(LocalDate.now().plusDays(1))
                .dropDate(LocalDate.now().plusDays(3))
                .pickupCity("Pune")
                .build();

        User user = new User();
        user.setUserId(1L);
        user.setEmail(userEmail);

        Car car = new Car();
        car.setCarId(carId);
        car.setCity("Pune");
        car.setStatus(CarStatus.AVAILABLE);
        car.setPricePerDay(100.0);
        car.setSeatingCapacity(5);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(userEmail);
        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        assertDoesNotThrow(() -> bookingService.createBooking(request));
    }
}
