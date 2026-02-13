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
import com.carRental.entity.PaymentStatus;
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

        // The core logic for making a reservation.
        // It performs several checks:
        // 1. Is the user valid?
        // 2. Has the user uploaded a driving license? (Crucial!)
        // 3. Is the car available for the chosen dates? (Overlapping check)
        // 4. Does the pickup city match where the car actually is?
        // 5. Are the dates valid (not in the past)?
        // If all good, it calculates the price and saves the booking as "PENDING".
        @Override
        public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {

                String email = SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getName();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                Car car = carRepository.findById(bookingRequestDTO.getCarId())
                                .orElseThrow(() -> new RuntimeException("Car not found"));

                // Validate Driving License
                if (user.getDrivingLicenceImage() == null || user.getDrivingLicenceImage().length == 0) {
                        throw new RuntimeException(
                                        "You must upload your Driving License in your Profile before booking.");
                }

                // Check for overlapping bookings
                boolean isBooked = bookingRepository.hasOverlappingBooking(
                                car.getCarId(),
                                bookingRequestDTO.getPickupDate(),
                                bookingRequestDTO.getDropDate());

                if (isBooked) {
                        throw new RuntimeException("Car is already booked for the selected dates.");
                }

                if (bookingRequestDTO.getPickupCity() != null &&
                                car.getCity() != null &&
                                !bookingRequestDTO.getPickupCity().equalsIgnoreCase(car.getCity())) {
                        throw new RuntimeException("Pickup city must match the car's location: "
                                        + car.getCity());
                }

                if (bookingRequestDTO.getPickupDate().isBefore(java.time.LocalDate.now())) {
                        throw new RuntimeException("Pickup date cannot be in the past");
                }

                // Calculate Total Cost: Days * Price Per Day
                long days = ChronoUnit.DAYS.between(
                                bookingRequestDTO.getPickupDate(),
                                bookingRequestDTO.getDropDate());

                double totalAmount = days * car.getPricePerDay();

                Booking booking = Booking.builder()
                                .pickupDate(bookingRequestDTO.getPickupDate())
                                .dropDate(bookingRequestDTO.getDropDate())
                                .bookingStatus(BookingStatus.PENDING) // Always starts as Pending until Admin approves
                                .paymentStatus(PaymentStatus.PENDING)
                                .totalAmount(totalAmount)
                                .user(user)
                                .car(car)
                                .build();

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

        // Admin Action: Changing the status (e.g., PENDING -> CONFIRMED or REJECTED).
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
                // Note: If rejected, the car becomes free for others because the intersection
                // query checks status.

                Booking savedBooking = bookingRepository.save(booking);
                return mapToResponseDTO(savedBooking);
        }

        /* ================= CANCEL BOOKING ================= */

        // User Action: Cancelling a reservation.
        @Override
        public BookingResponseDTO cancelBooking(Long bookingId) {

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new RuntimeException("Booking not found"));

                if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
                        throw new RuntimeException("Booking is already cancelled");
                }

                if (booking.getBookingStatus() == BookingStatus.REJECTED) {
                        throw new RuntimeException("Booking is already rejected");
                }

                booking.setBookingStatus(BookingStatus.CANCELLED);

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
                                                .drivingLicenceImage(user.getDrivingLicenceImage())
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
                                                .seatingCapacity(car.getSeatingCapacity())
                                                .fuelType(car.getFuelType())
                                                .carType(car.getCarType())
                                                .status(car.getStatus())
                                                .mapUrl(car.getLocation() != null ? car.getLocation().getMapUrl()
                                                                : null)
                                                .build())
                                .pickupDate(booking.getPickupDate())
                                .dropDate(booking.getDropDate())
                                .bookingStatus(booking.getBookingStatus())
                                .paymentStatus(booking.getPaymentStatus())
                                .totalAmount(booking.getTotalAmount())
                                .build();
        }
}
