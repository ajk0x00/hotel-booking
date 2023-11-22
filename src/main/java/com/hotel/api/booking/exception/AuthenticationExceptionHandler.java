package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.util.Logger;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationExceptionHandler {
    Logger logger = new Logger(this);

    @ExceptionHandler(UserAlreadyExistException.class)
    public ErrorDTO handleUserAlreadyExist(UserAlreadyExistException exception) {
        logger.logException(exception);
        return new ErrorDTO(1101, "User already exist");
    }

    @ExceptionHandler(JwtException.class)
    public ErrorDTO handleBadJwt(JwtException exception) {
        logger.logException(exception);
        return new ErrorDTO(1100, "Invalid token");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO handleAccessDenied(AccessDeniedException exception) {
        logger.logException(exception);
        return new ErrorDTO(1002, "User is unauthorized to access the resource");
    }
}
