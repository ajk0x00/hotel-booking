package com.hotel.api.booking.exception;

import com.hotel.api.booking.dto.ErrorDTO;
import com.hotel.api.booking.util.Logger;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    Logger logger = new Logger(this);

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleUserAlreadyExist(UserAlreadyExistException exception) {
        logger.logException(exception);
        return new ErrorDTO(1101, "User already exist");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public ErrorDTO handleBadJwt(JwtException exception) {
        logger.logException(exception);
        return new ErrorDTO(1102, "Invalid token");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorDTO handleAccessDenied(AccessDeniedException exception) {
        logger.logException(exception);
        return new ErrorDTO(1103, "User is unauthorized to access the resource");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO incorrectCredentials(BadCredentialsException exception) {
        logger.logException(exception);
        return new ErrorDTO(1103, "Username or password is incorrect");
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDTO unauthenticatedUserInAuthenticatedEndPoint(UnauthorizedUserException exception) {
        logger.logException(exception);
        return new ErrorDTO(1104, "User is not authorized to access this resource");
    }
}
