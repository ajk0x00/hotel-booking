package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.UserDTO;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.service.AuthenticationService;
import com.hotel.api.booking.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        User user = authService.signup(requestDTO, Authority.USER);
        String token = JwtService.generateToken(user);
        return new AuthenticationResponseDTO(token);
    }
}
