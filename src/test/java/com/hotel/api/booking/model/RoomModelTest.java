package com.hotel.api.booking.model;

import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class RoomModelTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Test
    @Transactional
    void shouldBeAbleToSaveValidData() {
        User user = new User(null, "ajk1@s6da.com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Hotel hotel = new Hotel(null, "Taj hotel", 100, location, user, new HashSet<>(), new HashSet<>());
        Room room = new Room(null, 101, RoomType.SINGLE, 1000, RoomStatus.AVAILABLE, hotel, new HashSet<>());

        Executable executable = () -> roomRepo.save(room);

        assertDoesNotThrow(executable);
    }

    @Test
    @Transactional
    void shouldNotBeAbleToSaveDataWithInvalidHotel() {
        User user = new User(null, "al@sadm.6com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Room room = new Room(null, 1001, RoomType.SINGLE, 1000, RoomStatus.AVAILABLE, null, new HashSet<>());
        roomRepo.save(room);
        Executable executable = () -> hotelRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }
}
