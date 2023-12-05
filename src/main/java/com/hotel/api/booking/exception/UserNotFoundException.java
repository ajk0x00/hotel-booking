package com.hotel.api.booking.exception;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(int code) {
        super(code);
    }
}
