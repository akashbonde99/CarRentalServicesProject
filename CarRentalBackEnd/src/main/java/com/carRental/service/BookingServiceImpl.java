package com.carRental.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carRental.dto.BookingRequestDTO;
import com.carRental.dto.BookingResponseDTO;
import com.carRental.dto.BookingStatusDTO;
import com.carRental.dto.CarDTO;
import com.carRental.dto.UserDTO;
import com.carRental.entity.Booking;
import com.carRental.entity.BookingStatus;
import com.carRental.entity.Car;
import com.carRental.entity.CarStatus;
import com.carRental.entity.User;
import com.carRental.repository.BookingRepository;
import com.carRental.repository.CarRepository;
import com.carRental.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

        private final BookingRepository bookingRepository;
        private final CarRepository carRepository;
        private final UserRepository userRepository;

        /* ================= CREATE BOOKING ================= */

        @Override
        public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Car car = carRepository.findById(bookingRequestDTO.getCarId())
                                .orElseThrow(() -> new RuntimeException("Car not found"));

                if (car.getStatus() != CarStatus.AVAILABLE) {
                        throw new RuntimeException("Car is not available for booking");
                }

                long days = ChronoUnit.DAYS.between(
                                bookingRequestDTO.getPickupDate(),
                                bookingRequestDTO.getDropDate());

                double totalAmount = days * car.getPricePerDay();

                Booking booking = Booking.builder()
                                .pickupDate(bookingRequestDTO.getPickupDate())
                                .dropDate(bookingRequestDTO.getDropDate())
                                .bookingStatus(BookingStatus.PENDING)
                                .totalAmount(totalAmount)
                                .user(user)
                                .car(car)
                                .build();

                car.setStatus(CarStatus.BOOKED);

                Booking savedBooking = bookingRepository.save(booking);

                return mapToResponseDTO(savedBooking);
        }

        /* ================= GET BOOKING BY ID ================= */

        @Override
        public BookingResponseDTO getBookingById(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                return mapToResponseDTO(booking);
        }

        /* ================= GET ALL BOOKINGS ================= */

        @Override
        public List<BookingResponseDTO> getAllBookings() {
                return bookingRepository.findAll().stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        /* ================= GET BOOKINGS BY CUSTOMER ================= */

        @Override
        public List<BookingResponseDTO> getBookingsByCustomer(Long customerId) {
                return bookingRepository.findByUser_UserId(customerId).stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        /* ================= GET BOOKINGS OF LOGGED-IN USER ================= */

        @Override
        public List<BookingResponseDTO> getBookingsForLoggedInUser() {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                return bookingRepository.findByUser_UserId(user.getUserId())
                                .stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        /* ================= GET BOOKING STATUS ================= */

        @Override
        public BookingStatusDTO getBookingStatus(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                return BookingStatusDTO.builder()
                                .bookingId(booking.getBookingId())
                                .bookingStatus(booking.getBookingStatus())
                                .build();
        }

        /* ================= UPDATE BOOKING STATUS ================= */

        @Override
        public BookingResponseDTO updateBookingStatus(Long bookingId, String status) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                BookingStatus newStatus;
                try {
                        newStatus = BookingStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid booking status");
                }

                booking.setBookingStatus(newStatus);

                // If rejected or cancelled, make the car available again
                if (newStatus == BookingStatus.REJECTED || newStatus == BookingStatus.CANCELLED) {
                        booking.getCar().setStatus(CarStatus.AVAILABLE);
                }

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponseDTO(savedBooking);
        }

        /* ================= CANCEL BOOKING ================= */

        @Override
        public BookingResponseDTO cancelBooking(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                booking.setBookingStatus(BookingStatus.CANCELLED);
                booking.getCar().setStatus(CarStatus.AVAILABLE);

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponseDTO(savedBooking);
        }

        /* ================= MAPPER ================= */

        private BookingResponseDTO mapToResponseDTO(Booking booking) {
                User user = booking.getUser();
                Car car = booking.getCar();

                return BookingResponseDTO.builder()
                                .bookingId(booking.getBookingId())
                                .carId(car.getCarId())
                                .user(UserDTO.builder()
                                                .userId(user.getUserId())
                                                .name(user.getName())
                                                .email(user.getEmail())
                                                .role(user.getRole())
                                                .drivingLicence(user.getDrivingLicence())
                                                .build())
                                .car(CarDTO.builder()
                                                .carId(car.getCarId())
                                                .image(car.getImage())
                                                .brand(car.getBrand())
                                                .model(car.getModel())
                                                .registrationNumber(car.getRegistrationNumber())
                                                .city(car.getCity())
                                                .pickupAddress(car.getPickupAddress())
                                                .description(car.getDescription())
                                                .pricePerDay(car.getPricePerDay())
                                                .fuelType(car.getFuelType())
                                                .carType(car.getCarType())
                                                .status(car.getStatus())
                                                .build())
                                .pickupDate(booking.getPickupDate())
                                .dropDate(booking.getDropDate())
                                .bookingStatus(booking.getBookingStatus())
                                .totalAmount(booking.getTotalAmount())
                                .build();
        }
}
