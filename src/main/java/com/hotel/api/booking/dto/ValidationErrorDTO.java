package com.hotel.api.booking.dto;

import java.util.Map;

public record ValidationErrorDTO(int code, Map<String, String> errors) {
}
