package com.hotel.api.booking.dto.request;

import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.model.RoomType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RoomInfoDTO(
        @NotNull @Min(value = 1, message = "Hotel should have at least one room") int roomNumber,
        @NotNull RoomType type,
        @NotNull double price,
        @NotNull RoomStatus status
) {
}
