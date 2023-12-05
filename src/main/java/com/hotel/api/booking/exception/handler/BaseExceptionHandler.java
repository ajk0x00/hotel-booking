package com.hotel.api.booking.exception.handler;

import com.hotel.api.booking.dto.response.ErrorResponseDTO;
import com.hotel.api.booking.dto.response.ValidationErrorResponseDTO;
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
    public ErrorResponseDTO handleAllUncaughtExceptions(Exception e) {
        logger.logException(e);
        return new ErrorResponseDTO(500, "Something went wrong :(");
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ValidationErrorResponseDTO handleBadRequest(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ValidationErrorResponseDTO(422, errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponseDTO handleBadRequests(HttpMessageNotReadableException e) {
        logger.logException(e);
        return new ErrorResponseDTO(400, "Bad request format");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponseDTO handleBadRequests(DataIntegrityViolationException e) {
        logger.logException(e);
        return new ErrorResponseDTO(400, "Bad request format");
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorResponseDTO handleBadRequests(HttpRequestMethodNotSupportedException e) {
        logger.logException(e);
        return new ErrorResponseDTO(405, "Bad request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorResponseDTO handleInvalidRequests(ConstraintViolationException exception) {
        StringBuffer errors = new StringBuffer();
        exception.getConstraintViolations()
                .forEach(violation -> errors.append(violation.getMessage()).append(","));
        return new ErrorResponseDTO(401, errors.toString());
    }

}
