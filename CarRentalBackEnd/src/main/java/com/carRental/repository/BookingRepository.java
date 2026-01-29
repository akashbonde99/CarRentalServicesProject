package com.carRental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carRental.entity.Booking;
import com.carRental.entity.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

        // Get all bookings of a user
        List<Booking> findByUser_UserId(Long userId);

        // Upcoming bookings of a user
        @Query("""
                            SELECT b FROM Booking b
                            WHERE b.user.userId = :userId
                              AND b.bookingStatus = :status
                              AND b.pickupDate >= CURRENT_DATE
                        """)
        List<Booking> findUpcomingBookings(
                        @Param("userId") Long userId,
                        @Param("status") BookingStatus status);

        @Query("""
                        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
                        FROM Booking b
                        WHERE b.car.carId = :carId
                          AND b.bookingStatus = 'CONFIRMED'
                          AND (
                              (b.pickupDate <= :dropDate AND b.dropDate >= :pickupDate)
                          )
                        """)
        boolean hasOverlappingBooking(
                        @Param("carId") Long carId,
                        @Param("pickupDate") java.time.LocalDate pickupDate,
                        @Param("dropDate") java.time.LocalDate dropDate);
}
