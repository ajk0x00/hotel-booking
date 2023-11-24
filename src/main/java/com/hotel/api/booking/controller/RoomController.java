package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.AvailabilityCheckDTO;
import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.dto.RoomDTO;
import com.hotel.api.booking.dto.RoomRequestDTO;
import com.hotel.api.booking.exception.*;
import com.hotel.api.booking.model.*;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.util.GeneralUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Tag(name = "Rooms API", description = "API endpoints for managing rooms")
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(summary = "List all available rooms in a specified date range")
    @GetMapping("/available")
    public List<RoomDTO> availableRooms(@PathVariable Long hotelId, AvailabilityCheckDTO checkDTO) {
        if (checkDTO.checkOut().before(checkDTO.checkIn()))
            throw new CheckOutBeforeCheckInException();
        if (checkDTO.checkIn().before(new Date(System.currentTimeMillis())))
            throw new CheckInInPastException();
        List<Booking> allByHotelIdAndDate = bookingRepo.findAllByHotelIdAndDate(hotelId, checkDTO.checkIn(), checkDTO.checkOut());
        Set<Long> collect = allByHotelIdAndDate
                .stream().map(booking -> booking.getRoom().getId())
                .collect(Collectors.toSet());
        return roomRepo.findAllByHotelId(hotelId)
                .stream().filter(room -> !collect.contains(room.getId()))
                .filter(room -> room.getStatus().equals(RoomStatus.AVAILABLE))
                .map(room -> new RoomDTO(room.getId(), room.getRoomNumber(), room.getType(), room.getPrice(), room.getStatus()))
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
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bookingUser = hotel.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.HOTEL.name()) &&
                !currentUser.getEmail().equals(bookingUser.getEmail()))
            throw new UnauthorizedUserException();
        if (roomRepo.getRoomCountByHotelId(hotelId) >= hotel.getRoomCount())
            throw new HotelMaximumRoomCountExceededException();
        Room room = new Room();
        GeneralUtils.map(roomDTO, room, false);
        room.setHotel(hotel);
        try {
            roomRepo.save(room);
            roomRepo.flush();
        } catch (DataIntegrityViolationException ignored) {
            throw new RoomAlreadyExistException();
        }
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
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bookingUser = hotel.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.HOTEL.name()) &&
                !currentUser.getEmail().equals(bookingUser.getEmail()))
            throw new UnauthorizedUserException();
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
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User hotelUser = hotel.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.HOTEL.name()) &&
                !currentUser.getEmail().equals(hotelUser.getEmail()))
            throw new UnauthorizedUserException();
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        if (hotel.getRooms().contains(targetRoom))
            throw new RoomNotFoundInHotelException();
        bookingRepo.deleteByRoomId(roomId);
        roomRepo.delete(targetRoom);
        return new EntityCreatedDTO(targetRoom.getId(), "Room deleted successfully");
    }
}
