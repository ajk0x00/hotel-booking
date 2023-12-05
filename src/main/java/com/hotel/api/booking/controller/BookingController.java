package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.BookingRequestDTO;
import com.hotel.api.booking.dto.response.BookingResponseDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.exception.BookingNotFoundException;
import com.hotel.api.booking.exception.CheckInInPastException;
import com.hotel.api.booking.exception.CheckOutBeforeCheckInException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.Booking;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.service.BookingService;
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

    private final BookingService bookingService;

    @Operation(summary = "List all bookings that belongs to a hotel/user")
    @Transactional
    @GetMapping("/bookings")
    public List<BookingResponseDTO> listAllBookingsOfSpecificHotel(@PathVariable Long hotelId) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getAuthority().equals(Authority.USER)) {
            return bookingService.getAllBookingsOfUserInAHotel(user.getId(), hotelId)
                    .stream().map(booking ->
                            new BookingResponseDTO(
                                    booking.getId(), booking.getRoom().getId(),
                                    booking.getGuestName(), booking.getContactInfo(),
                                    booking.getCheckIn(), booking.getCheckOut()))
                    .toList();
        }
        return bookingService.getAllBookingsInAHotel(hotelId)
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
        return bookingService.getAllBookingForRoom(hotelId, roomId)
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
        Booking booking = bookingService.getBookingDetails(hotelId, roomId, bookingId).orElseThrow(() -> new BookingNotFoundException(1344));
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

        if (bookingDTO.checkOut().before(bookingDTO.checkIn()))
            throw new CheckOutBeforeCheckInException(1309);
        if (bookingDTO.checkIn().before(new Date(System.currentTimeMillis())))
            throw new CheckInInPastException(1310);
        Booking booking = new Booking();
        GeneralUtils.map(bookingDTO, booking);
        booking = bookingService.createBooking(booking, hotelId, roomId);
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
        Booking booking = new Booking();
        GeneralUtils.map(bookingDTO, booking, false);
        bookingService.updateBooking(booking, hotelId, roomId, bookingId);
        return new EntityCreatedResponseDTO(bookingId, "Booking updated successfully");
    }

    @Operation(summary = "Cancel a specific booking")
    @Transactional
    @DeleteMapping("rooms/{roomId}/bookings/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedResponseDTO cancelBooking(@PathVariable Long bookingId,
                                                  @PathVariable Long roomId,
                                                  @PathVariable Long hotelId) {
        bookingService.deleteBooking(bookingId);
        return new EntityCreatedResponseDTO(bookingId, "Booking cancelled successfully");
    }
}
