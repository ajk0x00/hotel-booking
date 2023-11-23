package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.util.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private final Logger logger = new Logger(this);

    @ExceptionHandler(HotelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleHotelNotFound(HotelNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(1200, "Hotel not found");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ErrorDTO handleRoomAlreadyBooked(RoomAlreadyBookedException exception) {
        logger.logException(exception);
        return new ErrorDTO(1201, "Requested room is already booked");
    }

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleRoomNotFound(RoomNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(1202, "Room not found");
    }

    @ExceptionHandler(RoomNotFoundInHotelException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleRoomNotFoundInHotel(RoomNotFoundInHotelException exception) {
        logger.logException(exception);
        return new ErrorDTO(1203, "Room requested is not found in Hotel");
    }

    @ExceptionHandler(RoomUnavailableException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDTO handleRoomUnavailable(RoomUnavailableException exception) {
        logger.logException(exception);
        return new ErrorDTO(1204, "Room unavailable");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleUserNotFound(UserNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(1205, "User not found");
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleBookingNotFound(BookingNotFoundException exception) {
        logger.logException(exception);
        return new ErrorDTO(1206, "Unable to find booking");
    }

    @ExceptionHandler(RoomAlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleRoomAlreadyExist(RoomAlreadyExistException exception) {
        return new ErrorDTO(1207, "Room with same room number already exist in the hotel");
    }
}
