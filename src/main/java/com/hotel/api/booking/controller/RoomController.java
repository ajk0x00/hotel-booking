package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.AvailabilityCheckRequestDTO;
import com.hotel.api.booking.dto.request.RoomInfoDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.dto.response.RoomResponseDTO;
import com.hotel.api.booking.exception.CheckInInPastException;
import com.hotel.api.booking.exception.CheckOutBeforeCheckInException;
import com.hotel.api.booking.exception.RoomNotFoundException;
import com.hotel.api.booking.model.Room;
import com.hotel.api.booking.service.RoomService;
import com.hotel.api.booking.util.GeneralUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@Tag(name = "Rooms API", description = "API endpoints for managing rooms")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/rooms/")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "List all rooms in a specific hotel")
    @GetMapping("/")
    public List<RoomResponseDTO> listRoom(@PathVariable Long hotelId) {
        return roomService.getAllRoomsInHotel(hotelId)
                .stream().map(room ->
                        new RoomResponseDTO(room.getId(), room.getRoomNumber(),
                                room.getType(), room.getPrice(), room.getStatus()))
                .toList();
    }

    @Operation(summary = "List all available rooms in a specified date range")
    @GetMapping("/available")
    public List<RoomResponseDTO> availableRooms(@PathVariable Long hotelId, AvailabilityCheckRequestDTO checkDTO) {
        if (checkDTO.checkOut().before(checkDTO.checkIn()))
            throw new CheckOutBeforeCheckInException(1101);
        if (checkDTO.checkIn().before(new Date(System.currentTimeMillis())))
            throw new CheckInInPastException(1102);
        return roomService.getAvailableRooms(hotelId, checkDTO.checkIn(), checkDTO.checkOut())
                .stream()
                .map(room -> new RoomResponseDTO(room.getId(), room.getRoomNumber(), room.getType(), room.getPrice(), room.getStatus()))
                .toList();
    }

    @Operation(summary = "Get information about a specific Room")
    @GetMapping("/{roomId}")
    public RoomResponseDTO getRoomDetails(@PathVariable Long hotelId,
                                          @PathVariable Long roomId) {
        Room room = roomService.getRoomDetails(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1103));
        return new RoomResponseDTO(room.getId(), room.getRoomNumber(),
                room.getType(), room.getPrice(), room.getStatus());
    }

    @Operation(summary = "Create a new Room inside a specific hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedResponseDTO createRoom(@Valid @RequestBody RoomInfoDTO roomDTO,
                                               @PathVariable Long hotelId) {
        Room room = new Room();
        GeneralUtils.map(roomDTO, room, false);
        room = roomService.createRoom(room, hotelId);
        return new EntityCreatedResponseDTO(room.getId(), "Room created successfully");
    }

    @Operation(summary = "Update details about a specific room")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO updateRoom(@Valid @RequestBody RoomInfoDTO sourceRoomDTO,
                                               @PathVariable Long hotelId,
                                               @PathVariable Long roomId) {
        Room targetRoom = new Room();
        GeneralUtils.map(sourceRoomDTO, targetRoom, false);
        roomService.updateRoom(targetRoom, roomId, hotelId);
        return new EntityCreatedResponseDTO(roomId, "Room updated successfully");
    }

    @Operation(summary = "Delete a room")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO deleteRoom(@PathVariable Long hotelId,
                                               @PathVariable Long roomId) {
        roomService.deleteRoom(hotelId, roomId);
        return new EntityCreatedResponseDTO(roomId, "Room deleted successfully");
    }
}
