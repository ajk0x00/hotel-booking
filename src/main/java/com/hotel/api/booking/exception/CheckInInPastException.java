package com.hotel.api.booking.exception;

public class CheckInInPastException extends ApplicationException {
    public CheckInInPastException(int code) {
        super(code);
    }
}
