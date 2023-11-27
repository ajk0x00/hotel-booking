package com.hotel.api.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.api.booking.dto.request.HotelCreateRequestDTO;
import com.hotel.api.booking.dto.request.RoomInfoDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import com.hotel.api.booking.dto.response.EntityCreatedResponseDTO;
import com.hotel.api.booking.model.GeoLocation;
import com.hotel.api.booking.model.RoomStatus;
import com.hotel.api.booking.model.RoomType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RoomControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static long hotelId = -1;
    private static long roomId = -1;

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
    public void createNewHotel() throws Exception {
        if (hotelId == -1) {
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
                        RoomControllerTest.hotelId = dto.id();
                        System.out.println(hotelId);
                    }).andReturn();
        }

        assertNotEquals(hotelId, -1L);
    }

    @Test
    @Order(2)
    @WithUserDetails(value = "admin@admin.com", userDetailsServiceBeanName = "userDetailsService")
    void adminShouldBeAbleToCreateRoomOnValidInput() throws Exception {
        RoomInfoDTO requestDTO = new RoomInfoDTO(
                1,
                RoomType.SINGLE,
                1000,
                RoomStatus.AVAILABLE
        );

        mockMvc.perform(post("/api/v1/hotels/" + hotelId + "/rooms/")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andDo(result -> {
                    EntityCreatedResponseDTO dto = mapper.readValue(result.getResponse().getContentAsString(), EntityCreatedResponseDTO.class);
                    RoomControllerTest.roomId = dto.id();
                    assertNotEquals(-1, RoomControllerTest.roomId);
                }).andReturn();
    }
}
