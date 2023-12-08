package com.hotel.api.booking.dto.request;

import com.hotel.api.booking.model.GeoLocation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HotelUpdateRequestDTO(
        @NotNull @NotBlank String name,
        @NotNull @Min(value = 1, message = "Hotel should have at least one room") int roomCount,
        @NotNull @Valid GeoLocation location
) {
}
