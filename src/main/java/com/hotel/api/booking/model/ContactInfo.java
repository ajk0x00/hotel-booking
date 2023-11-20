package com.hotel.api.booking.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class ContactInfo {

    @NotNull
    @NotBlank
    String address;

    @NotNull
    @Min(value = 1000000000L, message = "phone number should be valid")
    @Max(value = 9999999999L, message = "phone number should be valid")
    Long phone;
}
