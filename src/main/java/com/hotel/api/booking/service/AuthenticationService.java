package com.hotel.api.booking.service;

import com.hotel.api.booking.dto.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.SignupRequestDTO;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponseDTO signup(SignupRequestDTO requestDTO, Authority authority) {
        String name = requestDTO.name();
        String email = requestDTO.email();
        String password = passwordEncoder.encode(requestDTO.password());

        User user = new User(
                name,
                email,
                password,
                Authority.USER
        );
        userRepo.save(user);
        String token = JwtService.generateToken(user);
        return new AuthenticationResponseDTO(token);
    }

    public AuthenticationResponseDTO login(AuthenticationRequestDTO requestDTO) {
        String username = requestDTO.email();
        String password = requestDTO.password();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        User user = userRepo.findByEmail(username).orElseThrow(); // TODO: throw a valid Exception
        String token = JwtService.generateToken(user);

        return new AuthenticationResponseDTO(token);
    }
}
