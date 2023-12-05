package com.hotel.api.booking.exception;

public class RoomNotFoundInHotelException extends ApplicationException {
    public RoomNotFoundInHotelException(int code) {
        super(code);
    }
}
