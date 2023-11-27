package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.HotelCreateRequestDTO;
import com.hotel.api.booking.dto.request.HotelUpdateRequestDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.dto.response.HotelResponseDTO;
import com.hotel.api.booking.exception.HotelAlreadyExistException;
import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.exception.UnauthorizedUserException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public Page<Hotel> listAllHotels(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        page = (page == null) ? 0 : page;
        size = (size == null) ? 10 : size;
        PageRequest pageRequestData = PageRequest.of(page, size);
        return hotelRepo.findAll(pageRequestData);
    }

    @Operation(summary = "Get details of a specific hotel")
    @GetMapping("/{id}")
    public HotelResponseDTO getHotelDetails(@PathVariable Long id) {
        Hotel hotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
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
            User user = authService.signup(userDTO, Authority.HOTEL);
            Hotel hotel = new Hotel();
            GeneralUtils.map(hotelCreate, hotel, false);
            hotel.setUser(user);
            hotelRepo.save(hotel);
            return new EntityCreatedResponseDTO(hotel.getId(), "Hotel created successfully");
        } catch (DataIntegrityViolationException exception) {
            throw new HotelAlreadyExistException();
        }
    }

    @Operation(summary = "Edit an already existing hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO updateHotel(@Valid @RequestBody HotelUpdateRequestDTO sourceHotel, @PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getAuthority().equals(Authority.HOTEL) &&
                !currentUser.getEmail().equals(targetHotel.getUser().getEmail()))
            throw new UnauthorizedUserException();
        GeneralUtils.map(sourceHotel, targetHotel, false);
        hotelRepo.save(targetHotel);
        return new EntityCreatedResponseDTO(targetHotel.getId(), "Hotel updated successfully");
    }

    @Operation(summary = "Delete a hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO deleteHotel(@PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getAuthority().equals(Authority.HOTEL) &&
                !currentUser.getEmail().equals(targetHotel.getUser().getEmail()))
            throw new UnauthorizedUserException();
        bookingRepo.deleteByHotelId(id);
        roomRepo.deleteByHotelId(id);
        hotelRepo.delete(targetHotel);
        return new EntityCreatedResponseDTO(targetHotel.getId(), "Hotel deleted successfully");
    }
}
