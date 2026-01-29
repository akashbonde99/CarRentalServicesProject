package com.carRental.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ROLE_ADMIN / ROLE_USER

    @Column(unique = true)
    private String drivingLicence;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] drivingLicenceImage;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    /**
     * Indicates whether this user account is active/enabled.
     * For normal customers this is always true.
     * For admins, new registrations start as inactive and must be
     * approved by an existing admin via the admin dashboard.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
