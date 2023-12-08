package com.hotel.api.booking.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class GeoLocation {

    @NotNull
    @Min(value = -90, message = "latitude should be between -90 to 90")
    @Max(value = 90, message = "latitude should be between -90 to 90")
    private double latitude;

    @NotNull
    @Min(value = -180, message = "longitude should be between -90 to 90")
    @Max(value = 180, message = "longitude should be between -90 to 90")
    private double longitude;
}
