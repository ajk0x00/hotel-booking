package com.hotel.api.booking.repository;

import com.hotel.api.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByHotelId(Long hotelId);

    @Query("select room from Room room where room.id = :roomId and room.hotel.id = :hotelId")
    Optional<Room> findByRoomIdAndHotelId(Long hotelId, Long roomId);

    @Modifying
    @Query("delete from Room room where room.hotel.id = :id")
    void deleteByHotelId(Long id);
}
