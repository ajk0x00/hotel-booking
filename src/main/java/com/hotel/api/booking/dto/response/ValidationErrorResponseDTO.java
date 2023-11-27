package com.hotel.api.booking.dto.response;

import java.util.Map;

public record ValidationErrorResponseDTO(int code, Map<String, String> errors) {
}
