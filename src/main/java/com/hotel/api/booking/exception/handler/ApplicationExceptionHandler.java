package com.hotel.api.booking.exception.handler;

import com.hotel.api.booking.dto.response.ErrorResponseDTO;
import com.hotel.api.booking.exception.*;
import com.hotel.api.booking.util.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Date;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private final Logger logger = new Logger(this);

    @ExceptionHandler(HotelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleHotelNotFound(HotelNotFoundException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1200, "Hotel not found");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RoomAlreadyBookedException.class)
    public ErrorResponseDTO handleRoomAlreadyBooked(RoomAlreadyBookedException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1201, "Requested room is already booked");
    }

    @ExceptionHandler(RoomNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleRoomNotFound(RoomNotFoundException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1202, "Room not found");
    }

    @ExceptionHandler(RoomNotFoundInHotelException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleRoomNotFoundInHotel(RoomNotFoundInHotelException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1203, "Room requested is not found in Hotel");
    }

    @ExceptionHandler(RoomUnavailableException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponseDTO handleRoomUnavailable(RoomUnavailableException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1204, "Room unavailable");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleUserNotFound(UserNotFoundException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1205, "User not found");
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleBookingNotFound(BookingNotFoundException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1206, "Unable to find booking");
    }

    @ExceptionHandler(RoomAlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleRoomAlreadyExist(RoomAlreadyExistException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1207, "Room with same room number already exist in the hotel");
    }

    @ExceptionHandler(HotelAlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleRoomAlreadyExist(HotelAlreadyExistException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1207, "Hotel with same admin user already exist in database");
    }

    @ExceptionHandler(HotelMaximumRoomCountExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleHotelAtMaximumRoomLimit(HotelMaximumRoomCountExceededException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1208, "Unable to create new room. Hotel at its maximum room count");
    }

    @ExceptionHandler(CheckOutBeforeCheckInException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleCheckOutBeforeCheckIn(CheckOutBeforeCheckInException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1209, "Check-out date should be after check-in date");
    }

    @ExceptionHandler(CheckInInPastException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleCheckInInPast(CheckInInPastException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(1210, "Check in date should be after " + new Date(System.currentTimeMillis()));
    }
}
