package com.hotel.api.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDTO(
        @NotNull @NotBlank String email,
        @NotNull @NotBlank String password) {
}
