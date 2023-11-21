package com.hotel.api.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.api.booking.dto.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.UserDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class AuthenticationTest {
    @Autowired
    private ObjectMapper mapper;
    private static MockMvc mockMvc;

    @BeforeAll
    public static void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldLoginOnValidInput() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("admin@admin.com", "admin");

        var result = mockMvc.perform(
                        post("/api/v1/users/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        AuthenticationResponseDTO authenticationResponseDTO = mapper.readValue(contentAsString, AuthenticationResponseDTO.class);

        assertNotNull(authenticationResponseDTO.token());
    }

    @Test
    void shouldNotLoginOnInValidInput() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("wrong@admin.com", "admin");

        var result = mockMvc.perform(
                        post("/api/v1/users/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        AuthenticationResponseDTO authenticationResponseDTO = mapper.readValue(contentAsString, AuthenticationResponseDTO.class);

        assertNull(authenticationResponseDTO.token());
    }

    @Test
    void shouldSignUpOnValidInput() throws Exception {
        UserDTO newUser = new UserDTO("Test User", "test@user.com", "test@1231");

        var result = mockMvc.perform(
                        post("/api/v1/users/sign-up")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        AuthenticationResponseDTO authenticationResponseDTO = mapper.readValue(contentAsString, AuthenticationResponseDTO.class);

        assertNotNull(authenticationResponseDTO.token());
    }

    @Test
    void shouldNotSignUpOnInValidInput() throws Exception {
        UserDTO newUser = new UserDTO("Test User", "test@user.com", "test@1"); // password is not strong

        var result = mockMvc.perform(
                        post("/api/v1/users/sign-up")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(newUser)))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        AuthenticationResponseDTO authenticationResponseDTO = mapper.readValue(contentAsString, AuthenticationResponseDTO.class);

        assertNull(authenticationResponseDTO.token());
    }
}
