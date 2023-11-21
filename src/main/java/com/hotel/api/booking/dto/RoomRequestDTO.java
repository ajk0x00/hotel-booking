package com.hotel.api.booking.dto;

import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.model.RoomType;
import jakarta.validation.constraints.NotNull;

public record RoomRequestDTO(
        @NotNull int roomNumber,
        @NotNull RoomType type,
        @NotNull double price,
        @NotNull RoomStatus status
) {
}
