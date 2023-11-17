package com.hotel.api.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDTO(
        @NotBlank @NotNull String name,
        @NotBlank @Email String email,
        @NotNull @NotBlank String password
) {
}
