package com.hotel.api.booking.exception;

public class HotelMaximumRoomCountExceededException extends ApplicationException {
    public HotelMaximumRoomCountExceededException(int code) {
        super(code);
    }
}
