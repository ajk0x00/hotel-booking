package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.util.Logger;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private final Logger logger = new Logger(this);

    @ExceptionHandler(HotelNotFoundException.class)
    public ErrorDTO handleHotelNotFound(HotelNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(300, "Hotel not found");
    }

    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ErrorDTO handleRoomAlreadyBooked(RoomAlreadyBookedException exception) {
        logger.logException(exception);
        return new ErrorDTO(301, "Room unavailable");
    }

    @ExceptionHandler(RoomNotFoundException.class)
    public ErrorDTO handleRoomNotFound(RoomNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(300, "Room not found");
    }

    @ExceptionHandler(RoomNotFoundInHotelException.class)
    public ErrorDTO handleRoomNotFoundInHotel(RoomNotFoundInHotelException exception) {
        logger.logException(exception);
        return new ErrorDTO(300, "Room requested is not found in Hotel");
    }

    @ExceptionHandler(RoomUnavailableException.class)
    public ErrorDTO handleRoomUnavailable(RoomUnavailableException exception) {
        logger.logException(exception);
        return new ErrorDTO(300, "Room unavailable");
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ErrorDTO handleUserNotFound(UserNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(300, "User not found");
    }
}
