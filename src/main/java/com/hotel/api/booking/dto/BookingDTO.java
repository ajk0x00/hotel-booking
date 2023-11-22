package com.hotel.api.booking.dto;

import com.hotel.api.booking.model.ContactInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;

public record BookingDTO(
        Long id,
        Long roomId,
        @NotNull @NotBlank String guestName,
        @NotNull @Valid ContactInfo contactInfo,
        @NotNull Date checkIn,
        @NotNull Date checkOut) {
}
