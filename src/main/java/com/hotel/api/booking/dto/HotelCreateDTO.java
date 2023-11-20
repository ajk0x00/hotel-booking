package com.hotel.api.booking.dto;


import com.hotel.api.booking.model.GeoLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelCreateDTO(
        @NotNull @NotBlank String name,
        @NotNull int roomCount,
        @NotNull @Valid GeoLocation location, UserDTO user) {
}
