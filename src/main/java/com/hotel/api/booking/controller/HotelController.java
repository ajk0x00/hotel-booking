package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.HotelCreateRequestDTO;
import com.hotel.api.booking.dto.request.HotelUpdateRequestDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.dto.response.HotelResponseDTO;
import com.hotel.api.booking.exception.HotelAlreadyExistException;
import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.service.HotelService;
import com.hotel.api.booking.util.GeneralUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Hotels API", description = "API endpoints for managing hotels")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels")
public class HotelController {

    private final HotelService hotelService;


    @Operation(summary = "List all hotels in the database")
    @GetMapping("/")
    public Page<Hotel> listAllHotels(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 10 : size;
        PageRequest pageRequestData = PageRequest.of(page, size);
        return hotelService.getAllHotels(pageRequestData);
    }

    @Operation(summary = "Get details of a specific hotel")
    @GetMapping("/{id}")
    public HotelResponseDTO getHotelDetails(@PathVariable Long id) {
        Hotel hotel = hotelService.getHotel(id).orElseThrow(() -> new HotelNotFoundException(1001));
        return new HotelResponseDTO(hotel.getId(), hotel.getName(), hotel.getRoomCount(), hotel.getLocation());
    }

    @Operation(summary = "Create new hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityCreatedResponseDTO createHotel(@Valid @RequestBody HotelCreateRequestDTO hotelCreate) {
        UserDTO userDTO = hotelCreate.user();
        try {
            User staff = new User();
            Hotel hotel = new Hotel();
            GeneralUtils.map(userDTO, staff);
            GeneralUtils.map(hotelCreate, hotel, false);
            hotelService.createHotel(hotel, staff);
            return new EntityCreatedResponseDTO(hotel.getId(), "Hotel created successfully");
        } catch (DataIntegrityViolationException exception) {
            throw new HotelAlreadyExistException(1002);
        }
    }

    @Operation(summary = "Edit an already existing hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO updateHotel(@Valid @RequestBody HotelUpdateRequestDTO sourceHotelDTO, @PathVariable Long id) {
        Hotel sourceHotel = new Hotel();
        GeneralUtils.map(sourceHotelDTO, sourceHotel, false);
        hotelService.updateHotel(id, sourceHotel);
        return new EntityCreatedResponseDTO(id, "Hotel updated successfully");
    }

    @Operation(summary = "Delete a hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return new EntityCreatedResponseDTO(id, "Hotel deleted successfully");
    }
}
