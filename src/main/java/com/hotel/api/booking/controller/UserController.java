package com.hotel.api.booking.controller;

import com.hotel.api.booking.dto.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.SignupRequestDTO;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final AuthenticationService authService;

    @GetMapping("/login")
    public AuthenticationResponseDTO login(@RequestBody AuthenticationRequestDTO requestDTO) {
        return authService.login(requestDTO);
    }

    @GetMapping("/sign-up")
    public AuthenticationResponseDTO signup(@RequestBody SignupRequestDTO requestDTO) {
        return authService.signup(requestDTO, Authority.USER);
    }
}
