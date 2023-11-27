package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.BookingRequestDTO;
import com.hotel.api.booking.dto.response.BookingResponseDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
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

import java.util.Date;
import java.util.List;


@Tag(name = "Booking API", description = "API endpoints for managing booking")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/")
public class BookingController {

    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;

    @Operation(summary = "List all bookings that belongs to a hotel/user")
    @Transactional
    @GetMapping("/bookings")
    public List<BookingResponseDTO> listAllBookingsOfSpecificHotel(@PathVariable Long hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(HotelNotFoundException::new);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getAuthority().equals(Authority.USER)) {
            return bookingRepo.findAllByHotelIdAndUserId(hotelId, user.getId())
                    .stream().map(booking ->
                            new BookingResponseDTO(
                                    booking.getId(), booking.getRoom().getId(),
                                    booking.getGuestName(), booking.getContactInfo(),
                                    booking.getCheckIn(), booking.getCheckOut()))
                    .toList();
        }
        if (user.getAuthority().equals(Authority.HOTEL) &&
                !hotel.getUser().getEmail().equals(user.getEmail()))
            throw new UnauthorizedUserException();
        return bookingRepo.findAllByHotelId(hotelId)
                .stream().map(booking ->
                        new BookingResponseDTO(
                                booking.getId(), booking.getRoom().getId(),
                                booking.getGuestName(), booking.getContactInfo(),
                                booking.getCheckIn(), booking.getCheckOut()))
                .toList();
    }

    @Operation(summary = "List all bookings registered on a specific room")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('HOTEL')")
    @GetMapping("rooms/{roomId}/bookings")
    List<BookingResponseDTO> listAllBooking(@PathVariable Long hotelId, @PathVariable Long roomId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(HotelNotFoundException::new);
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (currentUser.getAuthority().equals(Authority.HOTEL) &&
                !hotel.getUser().getEmail().equals(currentUser.getEmail()))
            throw new UnauthorizedUserException();
        return bookingRepo.findAllUserBookingByRoomAndHotelId(hotelId, roomId)
                .stream()
                .map(booking -> new BookingResponseDTO(
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
    BookingResponseDTO getBookingDetails(@PathVariable Long hotelId,
                                         @PathVariable Long roomId,
                                         @PathVariable Long bookingId) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Booking booking = bookingRepo.findUserBookingByRoomAndHotelId(bookingId, hotelId,
                roomId).orElseThrow(BookingNotFoundException::new);
        if (currentUser.getAuthority().equals(Authority.USER))
            if (!currentUser.getEmail().equals(booking.getUser().getEmail()))
                throw new UnauthorizedUserException();
        return new BookingResponseDTO(
                booking.getId(), booking.getRoom().getId(),
                booking.getGuestName(), booking.getContactInfo(),
                booking.getCheckIn(), booking.getCheckOut());
    }

    @Operation(summary = "Book a room in a Hotel")
    @Transactional
    @PostMapping("rooms/{roomId}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    EntityCreatedResponseDTO createBooking(@Valid @RequestBody BookingRequestDTO bookingDTO,
                                           @PathVariable Long hotelId,
                                           @PathVariable Long roomId) {
        Room room = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(RoomNotFoundException::new);
        boolean isRoomUnAvailable = room.getStatus() == RoomStatus.UNAVAILABLE;
        boolean isRoomAlreadyBooked = bookingRepo.isRoomAlreadyBooked(roomId, bookingDTO.checkIn(), bookingDTO.checkOut());
        if (isRoomUnAvailable)
            throw new RoomUnavailableException();
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException();
        if (bookingDTO.checkOut().before(bookingDTO.checkIn()))
            throw new CheckOutBeforeCheckInException();
        if (bookingDTO.checkIn().before(new Date(System.currentTimeMillis())))
            throw new CheckInInPastException();
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(HotelNotFoundException::new);
        Booking booking = new Booking();
        GeneralUtils.map(bookingDTO, booking, false);
        booking.setHotel(hotel);
        booking.setUser(currentUser);
        booking.setRoom(room);
        bookingRepo.save(booking);
        return new EntityCreatedResponseDTO(booking.getId(), "Room Booked successfully");
    }

    @Operation(summary = "Update details of a booking")
    @Transactional
    @PutMapping("rooms/{roomId}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    EntityCreatedResponseDTO updateBooking(@Valid @RequestBody BookingRequestDTO bookingDTO,
                                           @PathVariable Long bookingId,
                                           @PathVariable Long hotelId,
                                           @PathVariable Long roomId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(BookingNotFoundException::new);
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
        return new EntityCreatedResponseDTO(booking.getId(), "Booking updated successfully");
    }

    @Operation(summary = "Cancel a specific booking")
    @Transactional
    @DeleteMapping("rooms/{roomId}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO cancelBooking(@PathVariable Long bookingId,
                                                  @PathVariable Long roomId,
                                                  @PathVariable Long hotelId) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(BookingNotFoundException::new);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User bookingUser = booking.getUser();
        String authority = currentUser.getAuthorities()
                .stream().findFirst().orElseThrow().getAuthority();
        if (authority.equals(Authority.USER.name()) &&
                !currentUser.getEmail().equals(bookingUser.getEmail()))
            throw new UnauthorizedUserException();
        bookingRepo.deleteById(bookingId);
        return new EntityCreatedResponseDTO(booking.getId(), "Booking cancelled successfully");
    }
}
