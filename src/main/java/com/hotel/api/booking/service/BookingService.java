package com.hotel.api.booking.service;

import com.hotel.api.booking.exception.*;
import com.hotel.api.booking.model.*;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class BookingService {


    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;

    public List<Booking> getAllBookingsOfUserInAHotel(Long userId, Long hotelId) {
        hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1300));
        return bookingRepo.findAllByHotelIdAndUserId(hotelId, userId);
    }

    public List<Booking> getAllBookingsInAHotel(Long hotelId) {
        return bookingRepo.findAllByHotelId(hotelId);
    }

    public List<Booking> getAllBookingForRoom(Long hotelId, Long roomId) {
        hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1302));
        roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1303));
        return bookingRepo.findAllUserBookingByRoomAndHotelId(hotelId, roomId);
    }

    public Optional<Booking> getBookingDetails(Long hotelId, Long roomId, Long bookingId) {
        hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1317));
        roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1318));
        return bookingRepo.findUserBookingByRoomAndHotelId(bookingId, hotelId, roomId);
    }

    public Booking createBooking(Booking booking, Long hotelId, Long roomId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1311));
        Room room = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1306));
        boolean isRoomUnAvailable = room.getStatus() == RoomStatus.UNAVAILABLE;
        boolean isRoomAlreadyBooked = bookingRepo.isRoomAlreadyBooked(roomId, booking.getCheckIn(), booking.getCheckOut());
        if (isRoomUnAvailable)
            throw new RoomUnavailableException(1307);
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException(1308);
        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        booking.setHotel(hotel);
        booking.setUser(currentUser);
        booking.setRoom(room);
        bookingRepo.save(booking);
        return booking;
    }

    public void updateBooking(Booking booking, Long hotelId, Long roomId, Long bookingId) {
        hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1319));
        roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1320));
        Booking targetBooking = bookingRepo.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(1312));
        boolean isRoomAlreadyBooked = bookingRepo.isRoomAlreadyBooked(roomId, bookingId, booking.getCheckIn(), booking.getCheckOut());
        if (isRoomAlreadyBooked)
            throw new RoomAlreadyBookedException(1315);
        GeneralUtils.map(booking, targetBooking, false);
        bookingRepo.save(targetBooking);
    }

    public void deleteBooking(Long bookingId) {
        bookingRepo.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(1316));
        bookingRepo.deleteById(bookingId);
    }
}
