package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.*;
import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.service.AuthenticationService;
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

import java.util.List;
import java.util.function.Supplier;

@Tag(name = "Hotels API", description = "API endpoints for managing hotels")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelRepository hotelRepo;
    private final AuthenticationService authService;
    private final RoomRepository roomRepo;
    private final Supplier<HotelNotFoundException> hotelNotFoundException = HotelNotFoundException::new;
    private final BookingRepository bookingRepo;


    @Operation(summary = "List all hotels in the database")
    @GetMapping("/")
    public List<HotelDTO> listAllHotels() {
        return hotelRepo.findAll()
                .stream()
                .map(hotel -> new HotelDTO(hotel.getId(), hotel.getName(),
                        hotel.getRoomCount(), hotel.getLocation()))
                .toList();
    }

    @Operation(summary = "Get details of a specific hotel")
    @GetMapping("/{id}")
    public HotelDTO getHotelDetails(@PathVariable Long id) {
        Hotel hotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
        return new HotelDTO(hotel.getId(), hotel.getName(), hotel.getRoomCount(), hotel.getLocation());
    }

    @Operation(summary = "Create new hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedDTO createHotel(@Valid @RequestBody HotelCreateDTO hotelCreate) {
        UserDTO userDTO = hotelCreate.user();
        User user = authService.signup(userDTO, Authority.HOTEL);
        Hotel hotel = new Hotel();
        GeneralUtils.map(hotelCreate, hotel, false);
        hotel.setUser(user);
        hotelRepo.save(hotel);
        return new EntityCreatedDTO(hotel.getId(), "Hotel created successfully");
    }

    @Operation(summary = "Edit an already existing hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO updateHotel(@Valid @RequestBody HotelRequestDTO sourceHotel, @PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
        GeneralUtils.map(sourceHotel, targetHotel, false);
        hotelRepo.save(targetHotel);
        return new EntityCreatedDTO(targetHotel.getId(), "Hotel updated successfully");
    }

    @Operation(summary = "Delete a hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO deleteHotel(@PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
        bookingRepo.deleteByHotelId(id);
        roomRepo.deleteByHotelId(id);
        hotelRepo.delete(targetHotel);
        return new EntityCreatedDTO(targetHotel.getId(), "Hotel deleted successfully");
    }
}
