package com.hotel.api.booking.service;

import com.hotel.api.booking.dto.request.AuthenticationRequestDTO;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    public User signup(String name, String email, String password, Authority authority) {
        User user = new User(
                name,
                email,
                passwordEncoder.encode(password),
                authority
        );
        userRepo.save(user);
        userRepo.flush();
        return user;
    }

    public User login(AuthenticationRequestDTO requestDTO) {
        String username = requestDTO.email().toLowerCase();
        String password = requestDTO.password();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        return userRepo.findByEmail(username).orElseThrow(() -> new BadCredentialsException("Username or password is invalid"));
    }
}
