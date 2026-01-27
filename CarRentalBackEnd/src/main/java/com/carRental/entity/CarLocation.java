package com.carRental.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "car_locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String address;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;
}
