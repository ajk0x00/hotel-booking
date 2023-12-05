package com.hotel.api.booking.exception;

public class CheckOutBeforeCheckInException extends ApplicationException {
    public CheckOutBeforeCheckInException(int code) {
        super(code);
    }
}
