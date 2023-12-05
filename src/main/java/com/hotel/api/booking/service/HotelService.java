package com.hotel.api.booking.service;

import com.hotel.api.booking.exception.HotelNotFoundException;
import com.hotel.api.booking.model.Authority;
import com.hotel.api.booking.model.Hotel;
import com.hotel.api.booking.model.User;
import com.hotel.api.booking.repository.BookingRepository;
import com.hotel.api.booking.repository.HotelRepository;
import com.hotel.api.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class HotelService {

    private final HotelRepository hotelRepo;
    private final AuthenticationService authService;
    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;

    public Page<Hotel> getAllHotels(PageRequest request) {
        return hotelRepo.findAll(request);
    }

    public Optional<Hotel> getHotel(Long id) {
        return hotelRepo.findById(id);
    }

    public void createHotel(Hotel hotel, User hotelStaff) {
        hotelStaff = authService.signup(hotelStaff.getName(), hotelStaff.getEmail(),
                hotelStaff.getPassword(), Authority.HOTEL);
        hotel.setUser(hotelStaff);
        hotelRepo.save(hotel);
    }

    public void updateHotel(Long id, Hotel sourceHotel) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(() -> new HotelNotFoundException(1003));
        targetHotel.setName(sourceHotel.getName());
        targetHotel.setRoomCount(sourceHotel.getRoomCount());
        targetHotel.setLocation(sourceHotel.getLocation());
        hotelRepo.save(targetHotel);
    }

    public void deleteHotel(Long id) {
        Hotel targetHotel = hotelRepo.findById(id).orElseThrow(() -> new HotelNotFoundException(1005));
        bookingRepo.deleteByHotelId(id);
        roomRepo.deleteByHotelId(id);
        hotelRepo.delete(targetHotel);
    }
}
