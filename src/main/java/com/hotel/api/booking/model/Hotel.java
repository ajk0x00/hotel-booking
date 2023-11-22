package com.hotel.api.booking.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @NotNull
    @NotBlank
    String name;

    @NotNull
    int roomCount;

    @Embedded
    @NotNull
    GeoLocation location;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    Set<Booking> bookings = new HashSet<>();
}
