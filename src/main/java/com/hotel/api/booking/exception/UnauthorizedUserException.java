package com.hotel.api.booking.exception;

public class UnauthorizedUserException extends ApplicationException {
    public UnauthorizedUserException(int code) {
        super(code);
    }
}
