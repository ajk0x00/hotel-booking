package com.hotel.api.booking.dto;

public record ErrorDTO(int code, String[] errors) {
    public ErrorDTO(int code, String error) {
        this(code, new String[]{error});
    }
}
