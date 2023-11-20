package com.hotel.api.booking.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

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

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "hotel_id")
    Hotel hotel;
}
