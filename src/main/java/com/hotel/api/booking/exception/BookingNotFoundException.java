package com.hotel.api.booking.exception;

public class BookingNotFoundException extends ApplicationException {
    public BookingNotFoundException(int code) {
        super(code);
    }
}
