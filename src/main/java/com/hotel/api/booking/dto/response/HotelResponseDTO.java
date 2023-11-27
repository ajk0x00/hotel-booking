package com.hotel.api.booking.dto.response;

import com.hotel.api.booking.model.GeoLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelResponseDTO(
        Long id,
        @NotNull @NotBlank String name,
        @NotNull int roomCount,
        @NotNull @Valid GeoLocation location) {
}
