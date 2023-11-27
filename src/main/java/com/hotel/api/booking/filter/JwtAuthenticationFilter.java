package com.hotel.api.booking.filter;

import com.hotel.api.booking.config.SecurityConfig;
import com.hotel.api.booking.exception.UnauthorizedUserException;
import com.hotel.api.booking.service.JwtService;
import com.hotel.api.booking.util.Logger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.regex.Pattern;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver exceptionResolver;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService,
                                   @Qualifier("handlerExceptionResolver")
                                   HandlerExceptionResolver exceptionResolver) {
        this.userDetailsService = userDetailsService;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        Logger logger = new Logger(this);
        String authHeader = request.getHeader("Authorization");
        String pathSegment = request.getRequestURI();
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            for (Pattern pattern : SecurityConfig.publicEndPoints) {
                if (pattern.matcher(pathSegment).find()) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }
            exceptionResolver.resolveException(request, response, null,
                    new UnauthorizedUserException());
            return;
        }
        String token = authHeader.replace("Bearer ", "");

        try {
            JwtService jwtService = new JwtService(token);
            String username = jwtService.extractUsername();
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception exception) {
            logger.logException(exception);
            exceptionResolver.resolveException(request, response, null, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
