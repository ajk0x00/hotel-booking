package com.hotel.api.booking.exception;

public class HotelAlreadyExistException extends ApplicationException {
    public HotelAlreadyExistException(int code) {
        super(code);
    }
}
