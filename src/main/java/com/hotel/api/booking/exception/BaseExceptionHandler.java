package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.dto.ValidationErrorDTO;
import com.hotel.api.booking.util.Logger;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class BaseExceptionHandler {

    private final Logger logger = new Logger(this);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleAllUncaughtExceptions(Exception e) {
        logger.logException(e);
        return new ErrorDTO(1000, "Something went wrong :(");
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorDTO handleBadRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ValidationErrorDTO(1001, errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDTO handleBadRequests(HttpMessageNotReadableException e) {
        logger.logException(e);
        return new ErrorDTO(1002, "Bad request format");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorDTO handleBadRequests(DataIntegrityViolationException e) {
        logger.logException(e);
        return new ErrorDTO(1003, "Bad request format");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorDTO handleBadRequests(HttpRequestMethodNotSupportedException e) {
        logger.logException(e);
        return new ErrorDTO(1004, "Bad request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDTO handleInvalidRequests(ConstraintViolationException exception) {
        StringBuffer errors = new StringBuffer();
        exception.getConstraintViolations()
                .forEach(violation -> errors.append(violation.getMessage()).append(","));
        return new ErrorDTO(1001, errors.toString());
    }

}
