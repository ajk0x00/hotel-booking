package com.hotel.api.booking.dto.request;

import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record AvailabilityCheckRequestDTO(@NotNull Date checkIn, @NotNull Date checkOut) {
}
