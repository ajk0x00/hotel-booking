package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.request.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.response.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import com.hotel.api.booking.exception.UserAlreadyExistException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.service.AuthenticationService;
import com.hotel.api.booking.service.JwtService;
import com.hotel.api.booking.util.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Authentication API", description = "API endpoints for authentication")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthenticationService authService;
    private final Logger logger = new Logger(this);

    @Operation(summary = "Authenticate a user")
    @PostMapping("/login")
    public AuthenticationResponseDTO login(@Valid @RequestBody AuthenticationRequestDTO requestDTO) {
        User user = authService.login(requestDTO);
        String token = JwtService.generateToken(user);
        return new AuthenticationResponseDTO(token);
    }

    @Operation(summary = "Create a new user")
    @PostMapping("/sign-up")
    public AuthenticationResponseDTO signup(@Valid @RequestBody UserDTO requestDTO) {
        try {
            User user = authService.signup(requestDTO, Authority.USER);
            String token = JwtService.generateToken(user);
            return new AuthenticationResponseDTO(token);
        } catch (DataIntegrityViolationException exception) {
            logger.logException(exception);
            throw new UserAlreadyExistException();
        }
    }
}
