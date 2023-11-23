package com.hotel.api.booking.repository;

import com.hotel.api.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select booking from Booking booking where booking.hotel.id = :hotelId " +
            "and booking.room.id = :roomId")
    List<Booking> findAllUserBookingByRoomAndHotelId(Long hotelId, Long roomId);

    @Query("select booking from Booking booking where booking.id = :bookingId " +
            "and booking.hotel.id = :hotelId and booking.room.id = :roomId ")
    Optional<Booking> findUserBookingByRoomAndHotelId(Long bookingId, Long hotelId, Long roomId);

    @Query("select exists (select booking from Booking booking where booking.room.id = :roomId " +
            "and (booking.checkIn between :checkIn and :checkOut " +
            "or booking.checkOut between :checkIn and :checkOut))")
    boolean isRoomAlreadyBooked(Long roomId, Date checkIn, Date checkOut);

    @Query("select exists (select booking from Booking booking where booking.room.id = :roomId " +
            "and booking.id != :bookingId and (booking.checkIn between :checkIn and :checkOut " +
            "or booking.checkOut between :checkIn and :checkOut))")
    boolean isRoomAlreadyBooked(Long roomId, Long bookingId, Date checkIn, Date checkOut);

    @Query("select booking from Booking booking join booking.hotel hotel where hotel.id = :id")
    List<Booking> findAllByHotelId(Long id);

    @Modifying
    @Query("delete from Booking booking where booking.hotel.id = :id")
    void deleteByHotelId(Long id);

    @Modifying
    @Query("delete from Booking booking where booking.room.id = :id")
    void deleteByRoomId(Long id);
}
