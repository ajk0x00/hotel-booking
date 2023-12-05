package com.hotel.api.booking.repository;

import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    List<User> findByAuthority(Authority authority);

    @Query("select user from Hotel hotel join hotel.user user where hotel.id = :hotelId")
    Optional<User> findUserByHotelId(Long hotelId);

    @Query("select user from Booking booking join booking.user user where booking.id = :bookingId")
    Optional<User> findUserByBookingId(Long bookingId);
}
