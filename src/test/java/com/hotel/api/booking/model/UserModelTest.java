package com.hotel.api.booking.model;

import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class UserModelTest {

    @Autowired
    protected UserRepository userRepo;

    @Autowired
    protected HotelRepository hotelRepo;

    @Test
    void shouldBeAbleToSaveValidUserData() {
        User validUser = new User("test121", "test@admin.com", "test123567545", Authority.USER);

        userRepo.save(validUser);
        Executable executable = () -> userRepo.flush();

        assertDoesNotThrow(executable);
    }

    @Transactional
    @Test
    void shouldBeAbleToSaveUserDataWithInvalidEmail() {
        User validUser = new User("test121", "adminadmin.com", "teasdasdcsadst123", Authority.USER);
        userRepo.save(validUser);

        Executable executable = () -> userRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }

    @Transactional
    @Test
    void shouldBeAbleToSaveUserDataWithInvalidPassword() {
        User validUser = new User("test121", "adminadmin.com", "123", Authority.USER);
        userRepo.save(validUser);

        Executable executable = () -> userRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }

    @Transactional
    @Test
    void shouldBeAbleToSaveUserDataWithInvalidName() {
        User validUser = new User(null, "adminadmin.com", "123", Authority.USER);
        userRepo.save(validUser);

        Executable executable = () -> userRepo.flush();

        assertThrows(ConstraintViolationException.class, executable);
    }
}
