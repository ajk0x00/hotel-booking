package com.hotel.api.booking.exception.handler;

import com.hotel.api.booking.dto.response.ErrorResponseDTO;
import com.hotel.api.booking.exception.UnauthorizedUserException;
import com.hotel.api.booking.exception.UserAlreadyExistException;
import com.hotel.api.booking.exception.UserNotFoundException;
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

    private final Logger logger = new Logger(this);

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO handleUserAlreadyExist(UserAlreadyExistException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(exception.getCode(), "User already exist");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtException.class)
    public ErrorResponseDTO handleBadJwt(JwtException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(401, "Invalid token");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AccessDeniedException.class)
    public ErrorResponseDTO handleAccessDenied(AccessDeniedException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(401, "User is unauthorized to access the resource");
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO incorrectCredentials(BadCredentialsException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(401, "Username or password is incorrect");
    }

    @ExceptionHandler(UnauthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO unauthenticatedUserInAuthenticatedEndPoint(UnauthorizedUserException exception) {
        logger.logException(exception);
        return new ErrorResponseDTO(401, "User is not authorized to access this resource");
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDTO userNotFoundInUserDetailsService(UserNotFoundException e) {
        logger.logException(e);
        return new ErrorResponseDTO(401, "Authentication failed");
    }
}
