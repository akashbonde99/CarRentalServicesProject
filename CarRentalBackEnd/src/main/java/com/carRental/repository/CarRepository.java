package com.carRental.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carRental.entity.Car;
import com.carRental.entity.CarStatus;
import com.carRental.entity.CarType;
import com.carRental.entity.FuelType;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(CarStatus status);

    List<Car> findByCity(String city);

    List<Car> findByFuelType(FuelType fuelType);

    List<Car> findByCarType(CarType carType);

    @Query("""
            SELECT c FROM Car c
            WHERE (:city IS NULL OR LOWER(c.city) LIKE LOWER(CONCAT('%', :city, '%')))
            AND c.carId NOT IN (
                SELECT b.car.carId FROM Booking b
                WHERE b.bookingStatus = 'CONFIRMED'
                AND (b.pickupDate <= :dropDate AND b.dropDate >= :pickupDate)
            )
            """)
    List<Car> findAvailableCars(
            @Param("city") String city,
            @Param("pickupDate") java.time.LocalDate pickupDate,
            @Param("dropDate") java.time.LocalDate dropDate);
}
