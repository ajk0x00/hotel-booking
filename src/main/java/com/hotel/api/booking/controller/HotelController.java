package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.repository.HotelRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelRepository hotelRepo;

    @GetMapping("/")
    public List<Hotel> listAllHotels() {
        return hotelRepo.findAll();
    }

    @GetMapping("/{id}")
    public Hotel getHotelDetails(@PathVariable Long id) {
        return hotelRepo.findById(id).orElseThrow();
        // TODO: throw a valid Exception
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedDTO createHotel(@Valid @RequestBody Hotel hotel) {
        hotelRepo.save(hotel);
        return new EntityCreatedDTO(hotel.getId(), "Hotel created successfully");
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO updateHotel(@Valid @RequestBody Hotel sourceHotel, @PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow();
        // TODO: throw a valid variable
        targetHotel.setName(sourceHotel.getName());
        targetHotel.setRoomCount(sourceHotel.getRoomCount());
        targetHotel.setLocation(sourceHotel.getLocation());
        hotelRepo.save(targetHotel);
        return new EntityCreatedDTO(targetHotel.getId(), "Hotel updated successfully");
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO deleteHotel(@PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow();
        // TODO: throw a valid variable
        hotelRepo.delete(targetHotel);
        return new EntityCreatedDTO(targetHotel.getId(), "Hotel deleted successfully");
    }
}
