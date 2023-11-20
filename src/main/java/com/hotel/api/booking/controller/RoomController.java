package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.Room;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/rooms/")
public class RoomController {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;

    @GetMapping("/")
    public List<Room> listRoom(@PathVariable Long hotelId) {
        return roomRepo.findAllByHotelId(hotelId);
    }

    @GetMapping("/{roomId}")
    public Room getRoomDetails(@PathVariable Long hotelId, @PathVariable Long roomId) {
        return roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow();
        // TODO: throw a valid Exception
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedDTO createRoom(@Valid @RequestBody Room room, @PathVariable Long hotelId) {
        System.out.println(room);
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();
        // TODO: throw a valid Exception
        room.setHotel(hotel);
        roomRepo.save(room);
        return new EntityCreatedDTO(room.getId(), "Room created successfully");
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO updateRoom(@Valid @RequestBody Room sourceRoom,
                                       @PathVariable Long hotelId,
                                       @PathVariable Long roomId) {
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow();
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();
        // TODO: throw a valid Exception
        targetRoom.setRoomNumber(sourceRoom.getRoomNumber());
        targetRoom.setType(sourceRoom.getType());
        targetRoom.setPrice(sourceRoom.getPrice());
        targetRoom.setStatus(sourceRoom.getStatus());
        targetRoom.setHotel(hotel);
        roomRepo.save(targetRoom);
        return new EntityCreatedDTO(targetRoom.getId(), "Room updated successfully");
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{roomId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO deleteRoom(@PathVariable Long hotelId,
                                       @PathVariable Long roomId) {
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow();
        // TODO: throw a valid Exception
        roomRepo.delete(targetRoom);
        return new EntityCreatedDTO(targetRoom.getId(), "Room deleted successfully");
    }
}
