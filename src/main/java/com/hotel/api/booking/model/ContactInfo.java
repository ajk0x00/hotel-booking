package com.hotel.api.booking.model;

import jakarta.persistence.Embeddable;
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
    Long phone;
}
