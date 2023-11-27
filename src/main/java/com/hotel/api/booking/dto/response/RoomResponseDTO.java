package com.hotel.api.booking.dto.response;

import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.model.RoomType;
import jakarta.validation.constraints.NotNull;

public record RoomResponseDTO(
        Long id,
        @NotNull int roomNumber,
        @NotNull RoomType type,
        @NotNull double price,
        @NotNull RoomStatus status) {
}
