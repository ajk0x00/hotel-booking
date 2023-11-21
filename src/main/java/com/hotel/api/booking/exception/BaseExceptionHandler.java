package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.dto.ValidationErrorDTO;
import com.hotel.api.booking.util.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BaseExceptionHandler {

    Logger logger = new Logger(this);

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorDTO handleBadRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ValidationErrorDTO(101, errors);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleAllUncaughtExceptions(Exception e) {
        logger.logException(e);
        return new ErrorDTO(1000, "Something went wrong :(");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDTO handleBadRequests(HttpMessageNotReadableException e) {
        logger.logException(e);
        return new ErrorDTO(1001, "Bad request format");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDTO handleBadRequests(DataIntegrityViolationException e) {
        logger.logException(e);
        return new ErrorDTO(1002, "Bad request format");
    }
}
