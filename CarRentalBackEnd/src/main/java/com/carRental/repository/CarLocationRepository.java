package com.carRental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carRental.entity.CarLocation;

public interface CarLocationRepository extends JpaRepository<CarLocation, Long> {

    Optional<CarLocation> findByCar(com.carRental.entity.Car car);

    Optional<CarLocation> findByCar_CarId(Long carId);
}
