package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.dto.HotelCreateDTO;
import com.hotel.api.booking.dto.HotelDTO;
import com.hotel.api.booking.dto.UserDTO;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.service.AuthenticationService;
import com.hotel.api.booking.util.GeneralUtils;
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
    private final AuthenticationService authService;
    private final RoomRepository roomRepo;

    @GetMapping("/")
    public List<HotelDTO> listAllHotels() {
        return hotelRepo.findAll()
                .stream()
                .map(hotel -> new HotelDTO(hotel.getId(), hotel.getName(),
                        hotel.getRoomCount(), hotel.getLocation()))
                .toList();
    }

    @GetMapping("/{id}")
    public HotelDTO getHotelDetails(@PathVariable Long id) {
        Hotel hotel = hotelRepo.findById(id).orElseThrow();
        return new HotelDTO(hotel.getId(), hotel.getName(), hotel.getRoomCount(), hotel.getLocation());
        // TODO: throw a valid Exception
    }

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

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO updateHotel(@Valid @RequestBody HotelDTO sourceHotel, @PathVariable Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow();
        // TODO: throw a valid variable
        GeneralUtils.map(sourceHotel, targetHotel, false);
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
        roomRepo.deleteByHotelId(id);
        hotelRepo.delete(targetHotel);
        return new EntityCreatedDTO(targetHotel.getId(), "Hotel deleted successfully");
    }
}
