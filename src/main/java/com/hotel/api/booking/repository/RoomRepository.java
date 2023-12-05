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

    @Query("select room from Room room join room.hotel hotel where hotel.id = :hotelId")
    List<Room> findAllByHotelId(Long hotelId);

    @Query("select room from Room room join room.hotel hotel where hotel.id = :hotelId and room.id = :roomId")
    Optional<Room> findByRoomIdAndHotelId(Long hotelId, Long roomId);

    @Modifying
    @Query("delete from Room room where room.hotel.id = :id")
    void deleteByHotelId(Long id);

    @Modifying
    @Query("delete from Room room where room.id = :id")
    void deleteByRoomId(Long id);

    @Query("select count(room) from Room room where room.hotel.id = :id")
    int getRoomCountByHotelId(Long id);
}
