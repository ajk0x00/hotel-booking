package com.hotel.api.booking.exception;

public class RoomUnavailableException extends ApplicationException {
    public RoomUnavailableException(int code) {
        super(code);
    }
}
