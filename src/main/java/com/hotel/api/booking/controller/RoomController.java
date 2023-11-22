package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.dto.RoomDTO;
import com.hotel.api.booking.dto.RoomRequestDTO;
import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.exception.RoomNotFoundException;
import com.hotel.api.booking.exception.RoomNotFoundInHotelException;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.Room;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.util.GeneralUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/rooms/")
public class RoomController {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final BookingRepository bookingRepo;

    private final Supplier<RoomNotFoundException> roomNotFoundException = RoomNotFoundException::new;
    private final Supplier<HotelNotFoundException> hotelNotFoundException = HotelNotFoundException::new;


    @Operation(summary = "List all rooms in a specific hotel")
    @GetMapping("/")
    public List<RoomDTO> listRoom(@PathVariable Long hotelId) {
        return roomRepo.findAllByHotelId(hotelId)
                .stream().map(room ->
                        new RoomDTO(room.getId(), room.getRoomNumber(),
                                room.getType(), room.getPrice(), room.getStatus()))
                .toList();
    }


    @Operation(summary = "Get information about a specific Room")
    @GetMapping("/{roomId}")
    public RoomDTO getRoomDetails(@PathVariable Long hotelId,
                                  @PathVariable Long roomId) {
        Room room = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        return new RoomDTO(room.getId(), room.getRoomNumber(),
                room.getType(), room.getPrice(), room.getStatus());
    }


    @Operation(summary = "Create a new Room inside a specific hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedDTO createRoom(@Valid @RequestBody RoomRequestDTO roomDTO,
                                       @PathVariable Long hotelId) {
        // TODO: validate if the hotel id belongs to the authenticated user
        Room room = new Room();
        GeneralUtils.map(roomDTO, room, false);
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        room.setHotel(hotel);
        roomRepo.save(room);
        return new EntityCreatedDTO(room.getId(), "Room created successfully");
    }


    @Operation(summary = "Update details about a specific room")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO updateRoom(@Valid @RequestBody RoomRequestDTO sourceRoomDTO,
                                       @PathVariable Long hotelId,
                                       @PathVariable Long roomId) {
        // TODO: validate if the hotel id belongs to the authenticated user
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        if (hotel.getRooms().contains(targetRoom))
            throw new RoomNotFoundInHotelException();
        GeneralUtils.map(sourceRoomDTO, targetRoom, false);
        roomRepo.save(targetRoom);
        return new EntityCreatedDTO(targetRoom.getId(), "Room updated successfully");
    }


    @Operation(summary = "Delete a room")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO deleteRoom(@PathVariable Long hotelId,
                                       @PathVariable Long roomId) {
        // TODO: validate if the hotel id belongs to the authenticated user
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        if (hotel.getRooms().contains(targetRoom))
            throw new RoomNotFoundInHotelException();
        bookingRepo.deleteByRoomId(roomId);
        roomRepo.delete(targetRoom);
        return new EntityCreatedDTO(targetRoom.getId(), "Room deleted successfully");
    }
}
