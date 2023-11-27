package com.hotel.api.booking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank @NotNull String name,
        @NotBlank @Email String email,
        @NotNull @NotBlank @Size(min = 8, message = "password must have at least 8 characters")
        String password
) {
}
