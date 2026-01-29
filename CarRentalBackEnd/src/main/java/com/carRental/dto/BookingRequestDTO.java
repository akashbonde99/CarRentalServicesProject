package com.carRental.dto;

import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequestDTO {

    private Long carId;
    private LocalDate pickupDate;
    private LocalDate dropDate;
    private String pickupCity;
}
