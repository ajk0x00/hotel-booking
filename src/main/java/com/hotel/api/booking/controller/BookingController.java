package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.BookingDTO;
import com.hotel.api.booking.dto.BookingRequestDTO;
import com.hotel.api.booking.dto.EntityCreatedDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;


@Tag(name = "Booking API", description = "API endpoints for managing booking")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/")
public class BookingController {

    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final Supplier<RoomNotFoundException> roomNotFoundException = RoomNotFoundException::new;
    private final Supplier<HotelNotFoundException> hotelNotFoundException = HotelNotFoundException::new;
    private final Supplier<BookingNotFoundException> bookingNotFoundException = BookingNotFoundException::new;

    @Operation(summary = "List all bookings that belongs to a hotel")
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @GetMapping("/bookings")
    public List<BookingDTO> listAllBookingsOfSpecificHotel(@PathVariable Long hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getAuthority().equals(Authority.HOTEL) &&
                !hotel.getUser().getEmail().equals(user.getEmail()))
            throw new UnauthorizedUserException();
        return bookingRepo.findAllByHotelId(hotelId)
                .stream().map(booking ->
                        new BookingDTO(
                                booking.getId(), booking.getRoom().getId(),
                                booking.getGuestName(), booking.getContactInfo(),
                                booking.getCheckIn(), booking.getCheckOut()))
                .toList();
    }

    @Operation(summary = "List all bookings registered on a specific room")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @GetMapping("rooms/{roomId}/bookings")
    List<BookingDTO> listAllBooking(@PathVariable Long hotelId, @PathVariable Long roomId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (currentUser.getAuthority().equals(Authority.HOTEL) &&
                !hotel.getUser().getEmail().equals(currentUser.getEmail()))
            throw new UnauthorizedUserException();
        return bookingRepo.findAllUserBookingByRoomAndHotelId(hotelId, roomId)
                .stream()
                .map(booking -> new BookingDTO(
                        booking.getId(),
                        booking.getRoom().getId(),
                        booking.getGuestName(),
                        booking.getContactInfo(),
                        booking.getCheckIn(),
                        booking.getCheckOut()
                )).toList();
    }

    @Operation(summary = "Get details about a specific booking")
    @GetMapping("rooms/{roomId}/bookings/{bookingId}")
    BookingDTO getBookingDetails(@PathVariable Long hotelId,
                                 @PathVariable Long roomId,
                                 @PathVariable Long bookingId) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Booking booking = bookingRepo.findUserBookingByRoomAndHotelId(bookingId, hotelId,
                roomId).orElseThrow(bookingNotFoundException);
        if (currentUser.getAuthority().equals(Authority.USER))
            if (!currentUser.getEmail().equals(booking.getUser().getEmail()))
                throw new UnauthorizedUserException();
        return new BookingDTO(
                booking.getId(), booking.getRoom().getId(),
                booking.getGuestName(), booking.getContactInfo(),
                booking.getCheckIn(), booking.getCheckOut());
    }

    @Operation(summary = "Book a room in a Hotel")
    @Transactional
    @PostMapping("rooms/{roomId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    EntityCreatedDTO createBooking(@Valid @RequestBody BookingRequestDTO bookingDTO,
                                   @PathVariable Long hotelId,
                                   @PathVariable Long roomId) {
        Room room = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(roomNotFoundException);
        boolean isRoomUnAvailable = room.getStatus() == RoomStatus.UNAVAILABLE;
        boolean isRoomAlreadyBooked = bookingRepo.isRoomAlreadyBooked(roomId, bookingDTO.checkIn(), bookingDTO.checkOut());
        if (isRoomUnAvailable)
            throw new RoomUnavailableException();
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException();

        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(hotelNotFoundException);
        Booking booking = new Booking();
        GeneralUtils.map(bookingDTO, booking, false);
        booking.setHotel(hotel);
        booking.setUser(currentUser);
        booking.setRoom(room);
        bookingRepo.save(booking);
        return new EntityCreatedDTO(booking.getId(), "Room Booked successfully");
    }

    @Operation(summary = "Update details of a booking")
    @Transactional
    @PutMapping("rooms/{roomId}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    EntityCreatedDTO updateBooking(@Valid @RequestBody BookingRequestDTO bookingDTO,
                                   @PathVariable Long bookingId,
                                   @PathVariable Long hotelId,
                                   @PathVariable Long roomId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(bookingNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bookingUser = booking.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.USER.name()) &&
                !currentUser.getEmail().equals(bookingUser.getEmail()))
            throw new UnauthorizedUserException();
        Room room = booking.getRoom();
        boolean isRoomUnAvailable = room.getStatus() == RoomStatus.UNAVAILABLE;
        boolean isRoomAlreadyBooked = bookingRepo.isRoomAlreadyBooked(roomId, bookingId, bookingDTO.checkIn(), bookingDTO.checkOut());
        if (isRoomUnAvailable)
            throw new RoomUnavailableException();
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException();

        GeneralUtils.map(bookingDTO, booking, false);
        bookingRepo.save(booking);
        return new EntityCreatedDTO(booking.getId(), "Booking updated successfully");
    }

    @Operation(summary = "Cancel a specific booking")
    @Transactional
    @DeleteMapping("rooms/{roomId}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO cancelBooking(@PathVariable Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(bookingNotFoundException);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bookingUser = booking.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.USER.name()) &&
                !currentUser.getEmail().equals(bookingUser.getEmail()))
            throw new UnauthorizedUserException();
        return new EntityCreatedDTO(booking.getId(), "Booking cancelled successfully");
    }
}
