package com.hotel.api.booking.dto;

import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record AvailabilityCheckDTO(@NotNull Date checkIn, @NotNull Date checkOut) {
}
