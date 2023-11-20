package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.util.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BaseExceptionHandler {

    Logger logger = new Logger(this);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorDTO handleAllUncaughtExceptions(Exception e){
        logger.logException(e);
        return new ErrorDTO(1000, "Something went wrong :(");
    }
}
