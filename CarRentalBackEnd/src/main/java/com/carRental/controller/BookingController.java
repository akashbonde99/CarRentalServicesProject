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

    // Creates a new booking request.
    // The user selects a car and dates, and we verify if it's possible.
    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(
            @RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
        return ResponseEntity
                .ok(new ApiResponse<>("Booking created successfully", true, createdBooking));
    }

    // Fetches a specific booking.
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingById(
            @PathVariable Long id) {
        BookingResponseDTO booking = bookingService.getBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(new ApiResponse<>("Booking found", true, booking));
        }
        return ResponseEntity.status(404).body(new ApiResponse<>("Booking not found", false, null));
    }

    // Admin: View all bookings in the system to manage them.
    @GetMapping
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getAllBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(new ApiResponse<>("All bookings retrieved", true, bookings));
    }

    // Customer: "My Bookings" page.
    // Shows only the bookings belonging to the currently logged-in user.
    @GetMapping("/my-bookings")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookingsForLoggedInUser() {
        List<BookingResponseDTO> bookings = bookingService.getBookingsForLoggedInUser();
        return ResponseEntity.ok(new ApiResponse<>("Your bookings retrieved", true, bookings));
    }

    // Admin: Approve or Reject a booking.
    // Changes the status from PENDING to CONFIRMED (or REJECTED).
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

    // User/Admin: Cancel a booking.
    // If plans change, this sets the status to CANCELLED.
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
