package com.carRental.controller;

import java.util.List;

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
import com.carRental.dto.BookingRequestDTO;
import com.carRental.dto.BookingResponseDTO;
import com.carRental.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
        return ResponseEntity
                .ok(new ApiResponse<>("Booking created successfully", true, createdBooking));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @PathVariable Long id) {
        BookingResponseDTO booking = bookingService.getBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(new ApiResponse<>("Booking found", true, booking));
        }
        return ResponseEntity.status(404).body(new ApiResponse<>("Booking not found", false, null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(new ApiResponse<>("All bookings retrieved", true, bookings));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookingsByCustomer(
            @PathVariable Long customerId) {
        List<BookingResponseDTO> bookings = bookingService.getBookingsByCustomer(customerId);
        return ResponseEntity.ok(new ApiResponse<>("Customer bookings retrieved", true, bookings));
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookingsForLoggedInUser() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsForLoggedInUser();
        return ResponseEntity.ok(new ApiResponse<>("Your bookings retrieved", true, bookings));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingStatus(
            @PathVariable Long id, @PathVariable String status) {
        BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(id, status);
        if (updatedBooking != null) {
            return ResponseEntity
                    .ok(new ApiResponse<>("Booking status updated", true, updatedBooking));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Failed to update status", false, null));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(
            @PathVariable Long id) {
        BookingResponseDTO canceledBooking = bookingService.cancelBooking(id);
        if (canceledBooking != null) {
            return ResponseEntity
                    .ok(new ApiResponse<>("Booking cancelled successfully", true, canceledBooking));
        }
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>("Failed to cancel booking", false, null));
    }
}
