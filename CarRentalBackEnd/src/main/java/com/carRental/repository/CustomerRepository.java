package com.carRental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carRental.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
