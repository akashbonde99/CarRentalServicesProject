package com.carRental.dto;

import java.time.LocalDate;

import com.carRental.entity.BookingStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDTO {

    private Long bookingId;
    private Long carId;
    private UserDTO user;
    private CarDTO car;
    private LocalDate pickupDate;
    private LocalDate dropDate;
    private BookingStatus bookingStatus;
    private Double totalAmount;
}
