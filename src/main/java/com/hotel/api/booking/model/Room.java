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
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"roomNumber", "hotel_id"})})
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    private int roomNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomType type;

    @NotNull
    private double price;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoomStatus status;

    @NotNull
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Set<Booking> booking = new HashSet<>();
}
