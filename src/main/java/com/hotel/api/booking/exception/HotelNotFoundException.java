package com.hotel.api.booking.exception;

public class HotelNotFoundException extends ApplicationException {
    public HotelNotFoundException(int code) {
        super(code);
    }
}
