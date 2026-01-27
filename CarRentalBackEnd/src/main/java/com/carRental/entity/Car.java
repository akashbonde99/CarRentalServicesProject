package com.carRental.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "cars")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status; // AVAILABLE / BOOKED

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String pickupAddress;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double pricePerDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarType carType;

    @JsonIgnore
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @JsonIgnore
    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private CarLocation location;
}
