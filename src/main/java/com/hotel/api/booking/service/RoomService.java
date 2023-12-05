package com.hotel.api.booking.service;

import com.hotel.api.booking.exception.*;
import com.hotel.api.booking.model.Booking;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.Room;
import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.util.GeneralUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RoomService {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final BookingRepository bookingRepo;

    public List<Room> getAllRoomsInHotel(Long hotelId) {
        return roomRepo.findAllByHotelId(hotelId);
    }

    public Optional<Room> getRoomDetails(Long hotelId, Long roomId) {
        return roomRepo.findByRoomIdAndHotelId(hotelId, roomId);
    }

    public int getRoomCountInHotel(Long hotelId) {
        return roomRepo.getRoomCountByHotelId(hotelId);
    }

    public Room createRoom(Room room, Long hotelId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1104));
        if (getRoomCountInHotel(hotelId) >= hotel.getRoomCount())
            throw new HotelMaximumRoomCountExceededException(1106);
        room.setHotel(hotel);
        try {
            roomRepo.save(room);
            roomRepo.flush();
        } catch (DataIntegrityViolationException ignored) {
            throw new RoomAlreadyExistException(1107);
        }
        return room;
    }

    public void updateRoom(Room room, Long roomId, Long hotelId) {
        Room targetRoom = getRoomDetails(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1108));
        hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1109));
        GeneralUtils.map(room, targetRoom);
        roomRepo.save(targetRoom);
    }

    public void deleteRoom(Long hotelId, Long roomId) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow(() -> new HotelNotFoundException(1112));
        Room targetRoom = roomRepo.findByRoomIdAndHotelId(hotelId, roomId).orElseThrow(() -> new RoomNotFoundException(1114));
        if (!hotel.getRooms().contains(targetRoom))
            throw new RoomNotFoundInHotelException(1115);
        bookingRepo.deleteByRoomId(roomId);
        roomRepo.deleteByRoomId(targetRoom.getId());
    }

    public List<Room> getAvailableRooms(Long hotelId, Date checkIn, Date checkOut) {
        List<Booking> allByHotelIdAndDate = bookingRepo.findAllByHotelIdAndDate(hotelId, checkIn, checkOut);
        Set<Long> collect = allByHotelIdAndDate
                .stream().map(booking -> booking.getRoom().getId())
                .collect(Collectors.toSet());
        return getAllRoomsInHotel(hotelId)
                .stream().filter(room -> !collect.contains(room.getId()))
                .filter(room -> room.getStatus().equals(RoomStatus.AVAILABLE))
                .toList();

    }

}
