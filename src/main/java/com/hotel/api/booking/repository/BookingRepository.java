package com.hotel.api.booking.repository;

import com.hotel.api.booking.dto.BookingDTO;
import com.hotel.api.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select booking from Booking booking where booking.hotel.id = :hotelId " +
            "and booking.room.id = :roomId and booking.user.id = :userId")
    List<Booking> findAllUserBookingByRoomAndHotelId(Long hotelId, Long roomId, Long userId);

    @Query("select booking from Booking booking where booking.id = :bookingId " +
            "and booking.hotel.id = :hotelId and booking.room.id = :roomId " +
            "and booking.user.id = :userId")
    Optional<BookingDTO> findUserBookingByRoomAndHotelId(Long bookingId, Long hotelId, Long roomId, Long userId);

    @Query("select exists (select booking from Booking booking where booking.room.id = :roomId " +
            "and (booking.checkIn between :checkIn and :checkOut " +
            "or booking.checkOut between :checkIn and :checkOut))")
    boolean isRoomAlreadyBooked(Long roomId, Date checkIn, Date checkOut);

    @Query("select count(booking) from Booking booking where booking.room.id = :roomId " +
            "and (booking.checkIn between :checkIn and :checkOut " +
            "or booking.checkOut between :checkIn and :checkOut)")
    int countBookingOnDate(Long roomId, Date checkIn, Date checkOut);
}
