package com.hotel.api.booking.model;

import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class HotelModelTest {

    @Autowired
    protected UserRepository userRepo;

    @Autowired
    protected HotelRepository hotelRepo;

    @Test
    @Transactional
    void shouldBeAbleToSaveValidHotel() {
        User user = new User(null, "ajk1@sda.com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Hotel hotel = new Hotel(null, "Taj hotel", 100, location, user, new HashSet<>(), new HashSet<>());


        Executable executable = () -> hotelRepo.save(hotel);

        assertDoesNotThrow(executable);
    }

    @Test
    @Transactional
    void shouldNotBeAbleToSaveHotelWithInvalidName() {
        User user = new User(null, "al@sadm.com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Hotel hotel = new Hotel(null, null, 100, location, user, new HashSet<>(), new HashSet<>());
        hotelRepo.save(hotel);
        Executable executable = () -> hotelRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }

    @Test
    @Transactional
    void shouldNotBeAbleToSaveWithInValidLocation() {
        User user = new User(null, "ajk7@As.com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Hotel hotel = new Hotel(null, "Taj hotel", 100, null, user, new HashSet<>(), new HashSet<>());
        hotelRepo.save(hotel);
        Executable executable = () -> hotelRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }

    @Test
    @Transactional
    void shouldNotBeAbleToSaveWithInValidUser() {
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        Hotel hotel = new Hotel(null, "Taj hotel", 100, location, null, new HashSet<>(), new HashSet<>());
        hotelRepo.save(hotel);
        Executable executable = () -> hotelRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }
}
