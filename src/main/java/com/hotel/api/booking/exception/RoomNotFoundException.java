package com.hotel.api.booking.exception;

public class RoomNotFoundException extends ApplicationException {
    public RoomNotFoundException(int code) {
        super(code);
    }
}
