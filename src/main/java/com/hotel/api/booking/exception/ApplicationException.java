package com.hotel.api.booking.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {
    private final int code;

    ApplicationException(int code) {
        this.code = code;
    }

}
