package com.hotel.api.booking.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private int roomCount;

    @Embedded
    @NotNull
    private GeoLocation location;

    @JsonIgnore
    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Room> rooms = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Booking> bookings = new HashSet<>();
}
