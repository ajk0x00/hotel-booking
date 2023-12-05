package com.hotel.api.booking.exception;

public class RoomAlreadyExistException extends ApplicationException {
    public RoomAlreadyExistException(int code) {
        super(code);
    }
}
