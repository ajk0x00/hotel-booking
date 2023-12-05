package com.hotel.api.booking.exception;

public class RoomAlreadyBookedException extends ApplicationException {
    public RoomAlreadyBookedException(int code) {
        super(code);
    }
}
