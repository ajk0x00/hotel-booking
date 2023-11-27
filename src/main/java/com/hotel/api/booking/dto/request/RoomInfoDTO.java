package com.hotel.api.booking.dto.request;

import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.model.RoomType;
import jakarta.validation.constraints.NotNull;

public record RoomInfoDTO(
        @NotNull int roomNumber,
        @NotNull RoomType type,
        @NotNull double price,
        @NotNull RoomStatus status
) {
}
