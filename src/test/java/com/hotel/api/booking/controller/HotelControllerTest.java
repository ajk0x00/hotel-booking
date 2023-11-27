package com.hotel.api.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.api.booking.dto.request.HotelCreateRequestDTO;
import com.hotel.api.booking.dto.request.HotelUpdateRequestDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.model.GeoLocation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HotelControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static long hotelId = -1;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @Order(1)
    @WithMockUser(
            username = "admin@admin.com",
            password = "$2a$10$jD35eMtHF1wOU0LGNmwF3OkejI49aXRH5VjrtAlmJfufu3c183x6K",
            authorities = "ADMIN")
    void adminShouldBeAbleToCreateNewHotelOnCorrectRequest() throws Exception {
        GeoLocation location = new GeoLocation();
        location.setLongitude(122);
        location.setLatitude(320);
        HotelCreateRequestDTO hotelCreateRequestDTO = new HotelCreateRequestDTO(
                "Test hotel",
                300,
                location,
                new UserDTO("Test user", "test@hotel.com", "test1231")
        );

        mockMvc.perform(post("/api/v1/hotels/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(hotelCreateRequestDTO)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    EntityCreatedResponseDTO dto = mapper.readValue(result.getResponse().getContentAsString(), EntityCreatedResponseDTO.class);
                    HotelControllerTest.hotelId = dto.id();
                    System.out.println(hotelId);
                }).andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser(
            username = "admin@admin.com",
            password = "$2a$10$jD35eMtHF1wOU0LGNmwF3OkejI49aXRH5VjrtAlmJfufu3c183x6K",
            authorities = "ADMIN")
    void adminShouldNotBeAbleToCreateNewHotelOnInCorrectRequest() throws Exception {
        GeoLocation location = new GeoLocation();
        location.setLongitude(122);
        location.setLatitude(320);
        HotelCreateRequestDTO hotelCreateRequestDTO = new HotelCreateRequestDTO(
                "Test hotel",
                300,
                location,
                new UserDTO("Test user", "testhotel.com", "test1231")
        );

        mockMvc.perform(post("/api/v1/hotels/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(hotelCreateRequestDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Order(3)
    @WithMockUser(
            username = "test@hotel.com",
            password = "$2a$10$P92q9UEteKseC6MzYom92u3GWffeE0SuSscdvD1kMvwqL5CG8b8Hi",
            authorities = "HOTEL")
    void hotelShouldNotBeAbleToCreateNewHotelOnCorrectRequest() throws Exception {
        GeoLocation location = new GeoLocation();
        location.setLongitude(122);
        location.setLatitude(320);
        HotelCreateRequestDTO hotelCreateRequestDTO = new HotelCreateRequestDTO(
                "Test hotel",
                300,
                location,
                new UserDTO("Test user", "test@hotel.com", "test1231")
        );

        mockMvc.perform(post("/api/v1/hotels/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(hotelCreateRequestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    void shouldListAllHotels() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List res = mapper.readValue(result.getResponse().getContentAsString(), List.class);
                    res.forEach(Assertions::assertNotNull);
                });
    }

    @Test
    @Order(5)
    void shouldRespondWithNotFoundOnInvalidHotelID() throws Exception {
        mockMvc.perform(get("/api/v1/hotels/88898123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(6)
    @WithMockUser(
            username = "admin@admin.com",
            password = "$2a$10$jD35eMtHF1wOU0LGNmwF3OkejI49aXRH5VjrtAlmJfufu3c183x6K",
            authorities = "ADMIN")
    void adminShouldBeAbleToUpdateHotelOnCorrectRequest() throws Exception {
        GeoLocation location = new GeoLocation();
        location.setLongitude(122);
        location.setLatitude(320);
        HotelUpdateRequestDTO hotelCreateDTO = new HotelUpdateRequestDTO(
                "Edit Test hotel",
                300,
                location);
        mockMvc.perform(put("/api/v1/hotels/" + hotelId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(hotelCreateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    @WithMockUser(
            username = "test@hotel.com",
            password = "$2a$10$P92q9UEteKseC6MzYom92u3GWffeE0SuSscdvD1kMvwqL5CG8b8Hi",
            authorities = "HOTEL")
    void hotelShouldBeAbleToUpdateHotelOnCorrectRequest() throws Exception {
        GeoLocation location = new GeoLocation();
        location.setLongitude(122);
        location.setLatitude(320);
        HotelUpdateRequestDTO hotelCreateDTO = new HotelUpdateRequestDTO(
                "Hotel Edit Test hotel",
                300,
                location);
        mockMvc.perform(put("/api/v1/hotels/" + hotelId)
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(hotelCreateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @Order(8)
    @WithMockUser(
            username = "admin@admin.com",
            password = "$2a$10$jD35eMtHF1wOU0LGNmwF3OkejI49aXRH5VjrtAlmJfufu3c183x6K",
            authorities = "ADMIN")
    void adminShouldBeAbleToDeleteHotel() throws Exception {
        mockMvc.perform(delete("/api/v1/hotels/" + hotelId))
                .andExpect(status().isOk());
    }
}
