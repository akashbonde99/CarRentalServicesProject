package com.carRental.service;

import java.util.List;

import com.carRental.dto.BookingRequestDTO;
import com.carRental.dto.BookingResponseDTO;
import com.carRental.dto.BookingStatusDTO;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);

    BookingResponseDTO getBookingById(Long id);

    List<BookingResponseDTO> getAllBookings();

    List<BookingResponseDTO> getBookingsByCustomer(Long customerId);

    List<BookingResponseDTO> getBookingsForLoggedInUser();

    BookingStatusDTO getBookingStatus(Long bookingId);

    BookingResponseDTO updateBookingStatus(Long bookingId, String status);

    BookingResponseDTO cancelBooking(Long bookingId);
}
