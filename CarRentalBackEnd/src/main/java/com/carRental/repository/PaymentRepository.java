package com.carRental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carRental.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByBooking_BookingId(Long bookingId);
}
