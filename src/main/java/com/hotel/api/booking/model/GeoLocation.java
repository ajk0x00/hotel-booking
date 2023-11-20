package com.hotel.api.booking.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class GeoLocation {

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;
}
