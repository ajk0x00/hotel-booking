package com.hotel.api.booking.config;

import com.hotel.api.booking.exception.UserNotFoundException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepo;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByEmail(username).orElseThrow(() -> new UserNotFoundException(1445));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public void addAdminUser() {
        if (userRepo.findByAuthority(Authority.ADMIN).isEmpty())
            userRepo.save(new User(
                    "Hotel Booking Admin",
                    "admin@admin.com",
                    passwordEncoder().encode("admin"),
                    Authority.ADMIN
            ));
    }
}
