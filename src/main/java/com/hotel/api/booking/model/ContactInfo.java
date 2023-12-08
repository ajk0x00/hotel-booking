package com.hotel.api.booking.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactInfo {

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid phone number")
    private Long phone;
}
