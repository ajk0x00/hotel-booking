package com.hotel.api.booking.filter;

import com.hotel.api.booking.config.SecurityConfig;
import com.hotel.api.booking.exception.BookingNotFoundException;
import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.exception.UnauthorizedUserException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.UserRepository;
import com.hotel.api.booking.util.Logger;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class RoleAuthorizationFilter extends OncePerRequestFilter {

    private final Pattern hotelEndPoint = Pattern.compile("/api/v1/hotels/(\\d+)$");
    private final Pattern roomEndpoint = Pattern.compile("/api/v1/hotels/(\\d+)/rooms/(\\d*)$");
    private final Pattern bookingEndpoint = Pattern.compile("/api/v1/hotels/(\\d+)/rooms/(\\d+)/bookings/(\\d*)$");

    private final UserRepository userRepository;

    private final HandlerExceptionResolver exceptionResolver;
    private final Logger logger = new Logger(this);

    public RoleAuthorizationFilter(
            UserRepository userRepository,
            @Qualifier("handlerExceptionResolver")
            HandlerExceptionResolver exceptionResolver) {
        this.userRepository = userRepository;
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        String pathSegment = request.getRequestURI();
        for (Pattern pattern : SecurityConfig.publicEndPoints) {
            if (pattern.matcher(pathSegment).find()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String httpMethod = request.getMethod();
        Matcher hotelMatcher = hotelEndPoint.matcher(pathSegment);
        Matcher roomMatcher = roomEndpoint.matcher(pathSegment);
        Matcher bookingMatcher = bookingEndpoint.matcher(pathSegment);

        if (bookingMatcher.find() && List.of("GET", "PUT", "DELETE").contains(httpMethod))
            handleBookingAuthority(request, response, bookingMatcher, filterChain);
        else if (roomMatcher.find() && List.of("POST", "PUT", "DELETE").contains(httpMethod))
            handleRoomRequestAuthority(request, response, roomMatcher, filterChain);
        else if (hotelMatcher.find() && List.of("PUT", "DELETE").contains(httpMethod))
            handleHotelRequestAuthority(request, response, hotelMatcher, filterChain);
        else
            filterChain.doFilter(request, response);
    }

    private void handleHotelRequestAuthority(HttpServletRequest request, HttpServletResponse response, Matcher hotelMatcher, FilterChain filterChain) throws ServletException, IOException {
        Long hotelId = Long.parseLong(hotelMatcher.group(1));
        Optional<User> hotelStaff = userRepository.findUserByHotelId(hotelId);
        if (hotelStaff.isEmpty()) {
            Exception exception = new HotelNotFoundException(1500);
            logger.logException(exception);
            exceptionResolver.resolveException(request, response, null, exception);
            return;
        }
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getAuthority().equals(Authority.ADMIN) &&
                !currentUser.getEmail().equals(hotelStaff.get().getEmail())) {
            Exception exception = new UnauthorizedUserException(1501);
            logger.logException(exception);
            exceptionResolver.resolveException(request, response, null, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void handleRoomRequestAuthority(HttpServletRequest request, HttpServletResponse response, Matcher roomMatcher, FilterChain filterChain) throws ServletException, IOException {
        Long hotelId = Long.parseLong(roomMatcher.group(1));
        Optional<User> hotelStaff = userRepository.findUserByHotelId(hotelId);
        if (hotelStaff.isEmpty()) {
            Exception exception = new HotelNotFoundException(1502);
            logger.logException(exception);
            exceptionResolver.resolveException(request, response, null, exception);
            return;
        }
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!currentUser.getAuthority().equals(Authority.ADMIN) &&
                !currentUser.getEmail().equals(hotelStaff.get().getEmail())) {
            Exception exception = new UnauthorizedUserException(1503);
            logger.logException(exception);
            exceptionResolver.resolveException(request, response, null, exception);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void handleBookingAuthority(
            HttpServletRequest request,
            HttpServletResponse response,
            Matcher bookingMatcher, FilterChain filterChain) throws ServletException, IOException {

        String strBookingId = bookingMatcher.group(3);
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (strBookingId.isBlank()) {
            Long hotelId = Long.parseLong(bookingMatcher.group(1));
            Optional<User> hotelStaff = userRepository.findUserByHotelId(hotelId);
            if (hotelStaff.isEmpty()) {
                Exception exception = new HotelNotFoundException(1504);
                logger.logException(exception);
                exceptionResolver.resolveException(request, response, null, exception);
                return;
            }
            if (!currentUser.getAuthority().equals(Authority.ADMIN) &&
                    !currentUser.getEmail().equals(hotelStaff.get().getEmail())) {
                Exception exception = new UnauthorizedUserException(1505);
                logger.logException(exception);
                exceptionResolver.resolveException(request, response, null, exception);
                return;
            }
        } else {
            Long bookingId = Long.valueOf(strBookingId);
            Optional<User> bookingOwner = userRepository.findUserByBookingId(bookingId);
            if (bookingOwner.isEmpty()) {
                Exception exception = new BookingNotFoundException(1506);
                logger.logException(exception);
                exceptionResolver.resolveException(request, response, null, exception);
                return;
            }
            if (!currentUser.getAuthority().equals(Authority.ADMIN) &&
                    !currentUser.getEmail().equals(bookingOwner.get().getEmail())) {
                Exception exception = new UnauthorizedUserException(1507);
                logger.logException(exception);
                exceptionResolver.resolveException(request, response, null, exception);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
