package com.hotel.api.booking.model;

import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@ActiveProfiles("test")
public class BookingModelUnitTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HotelRepository hotelRepo;

    @Autowired
    private RoomRepository roomRepo;

    @Autowired
    private BookingRepository bookingRepo;

    @Test
    @Transactional
    void shouldSaveValidData() {
        User user = new User(null, "ajk11@sda.com", "asdnhkljasndlkjasd", Authority.USER);
        GeoLocation location = new GeoLocation();
        location.setLatitude(121);
        location.setLongitude(100);
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setAddress("Kannur");
        contactInfo.setPhone(9876543210L);
        Hotel hotel = new Hotel(null, "Imperial hotel", 100, location, user, new HashSet<>(), new HashSet<>());
        Room room = new Room(null, 101, RoomType.SINGLE, 1000, RoomStatus.AVAILABLE, hotel, new HashSet<>());
        Booking booking = new Booking(
                null, "Tester", contactInfo,
                hotel, room, user, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis() + 10928312L)
        );
        userRepo.save(user);
        hotelRepo.save(hotel);
        roomRepo.save(room);
        Executable executable = () -> bookingRepo.save(booking);
        assertDoesNotThrow(executable);
    }
}
