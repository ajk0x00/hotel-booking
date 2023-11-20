package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.BookingDTO;
import com.hotel.api.booking.dto.EntityCreatedDTO;
import com.hotel.api.booking.exception.*;
import com.hotel.api.booking.model.*;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.util.GeneralUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/hotels/{hotelId}/rooms/{roomId}/bookings")
public class BookingController {

    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final Supplier<RoomNotFoundException> roomNotFoundException = RoomNotFoundException::new;
    private final Supplier<HotelNotFoundException> hotelNotFoundException = HotelNotFoundException::new;
    private final Supplier<BookingNotFoundException> bookingNotFoundException = BookingNotFoundException::new;

    @GetMapping("/")
    List<BookingDTO> listAllBooking(@PathVariable Long hotelId, @PathVariable Long roomId) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return bookingRepo.findAllUserBookingByRoomAndHotelId(hotelId, roomId, currentUser.getId())
                .stream()
                .map(booking -> new BookingDTO(
                        booking.getId(),
                        booking.getGuestName(),
                        booking.getContactInfo(),
                        booking.getCheckIn(),
                        booking.getCheckOut()
                )).toList();
    }

    @GetMapping("/{bookingId}")
    BookingDTO getBookingDetails(@PathVariable Long hotelId,
                                 @PathVariable Long roomId,
                                 @PathVariable Long bookingId) {
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return bookingRepo.findUserBookingByRoomAndHotelId(bookingId, hotelId,
                roomId, currentUser.getId()).orElseThrow(bookingNotFoundException);
    }

    @Transactional
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    EntityCreatedDTO createBooking(@Valid @RequestBody BookingDTO bookingDTO,
                                   @PathVariable Long hotelId,
                                   @PathVariable Long roomId) {
        Room room = roomRepo.findByRoomIdAndHotelId(roomId, hotelId).orElseThrow(roomNotFoundException);
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

    @Transactional
    @PutMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    EntityCreatedDTO updateBooking(@Valid @RequestBody BookingDTO bookingDTO,
                                   @PathVariable Long bookingId,
                                   @PathVariable Long hotelId,
                                   @PathVariable Long roomId) {
        // TODO: check if the booking belongs to the user
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(bookingNotFoundException);
        Room room = booking.getRoom();
        boolean isRoomUnAvailable = room.getStatus() == RoomStatus.UNAVAILABLE;
        boolean isRoomAlreadyBooked = bookingRepo.countBookingOnDate(roomId, bookingDTO.checkIn(), bookingDTO.checkOut()) > 1;
        if (isRoomUnAvailable)
            throw new RoomUnavailableException();
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException();

        GeneralUtils.map(bookingDTO, booking, false);
        bookingRepo.save(booking);
        return new EntityCreatedDTO(booking.getId(), "Booking updated successfully");
    }

    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public EntityCreatedDTO cancelBooking(@PathVariable Long bookingId) {
        // TODO: check if the booking belongs to the user
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(bookingNotFoundException);
        return new EntityCreatedDTO(booking.getId(), "Booking cancelled successfully");
    }
}
