package com.hotel.api.booking.model;

import jakarta.persistence.*;
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
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    // TODO: Add a composite unique key with hotel
    @NotNull
    int roomNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    RoomType type;

    @NotNull
    double price;

    @NotNull
    @Enumerated(EnumType.STRING)
    RoomStatus status;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    Hotel hotel;

    @OneToMany(mappedBy = "room")
    Set<Booking> booking = new HashSet<>();
}
