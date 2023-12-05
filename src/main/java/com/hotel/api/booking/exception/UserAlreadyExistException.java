package com.hotel.api.booking.exception;

public class UserAlreadyExistException extends ApplicationException {
    public UserAlreadyExistException(int code) {
        super(code);
    }
}
