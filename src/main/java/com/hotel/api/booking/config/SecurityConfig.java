package com.hotel.api.booking.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.regex.Pattern;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationProvider authProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final HandlerExceptionResolver exceptionResolver;
    private final CorsConfigurationSource configurationSource;

    public static List<Pattern> publicEndPoints = List.of(
            Pattern.compile("/api/v1/users/login"),
            Pattern.compile("/api/v1/users/sign-up"),
            Pattern.compile("/swagger-ui/*"),
            Pattern.compile("/v3/api-docs"),
            Pattern.compile("/v3/api-docs/*")
    );

    public SecurityConfig(AuthenticationProvider authProvider,
                          JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Qualifier("handlerExceptionResolver")
                          HandlerExceptionResolver exceptionResolver,
                          @Qualifier("corsConfiguration")
                          CorsConfigurationSource configurationSource) {
        this.authProvider = authProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.exceptionResolver = exceptionResolver;
        this.configurationSource = configurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/api/v1/users/login").permitAll()
                        .requestMatchers("/api/v1/users/sign-up").permitAll()
                        .requestMatchers("/swagger-ui/*").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/v3/api-docs/*").permitAll()
                        .anyRequest().authenticated())
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        handle -> handle.accessDeniedHandler((req, res, exp) ->
                                exceptionResolver.resolveException(req, res, null, exp)))
                .cors(corsConfigurer -> corsConfigurer.configurationSource(configurationSource))
                .build();
    }
}
