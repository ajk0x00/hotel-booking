package com.hotel.api.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.api.booking.dto.request.AuthenticationRequestDTO;
import com.hotel.api.booking.dto.response.AuthenticationResponseDTO;
import com.hotel.api.booking.dto.request.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldReturnValidTokenForValidUser() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("admin@admin.com", "admin");
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    AuthenticationResponseDTO response =
                            mapper.readValue(result.getResponse().getContentAsString(),
                                    AuthenticationResponseDTO.class);
                    assertNotNull(response.token());
                });
    }

    @Test
    void shouldReturnUnauthorizedOnIncorrectCredentials() throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO("random@admin.com", "admin");
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnTokenOnValidSignupRequest() throws Exception {
        UserDTO requestDTO = new UserDTO("Tester", "tester@admin.com", "tester1234");
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andDo(result -> {
                    AuthenticationResponseDTO response =
                            mapper.readValue(result.getResponse().getContentAsString(),
                                    AuthenticationResponseDTO.class);
                    assertNotNull(response.token());
                });
    }

    @Test
    void shouldReturnForbiddenIfUserAlreadyExist() throws Exception {
        UserDTO requestDTO = new UserDTO("Tester", "admin@admin.com", "tester12345");
        mockMvc.perform(post("/api/v1/users/sign-up")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());
    }
}
