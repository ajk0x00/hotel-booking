package com.hotel.api.booking.dto;


import com.hotel.api.booking.model.GeoLocation;

public record HotelCreateDTO(String name, int roomCount,
                             GeoLocation location, UserDTO user) {
}
