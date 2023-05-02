package com.example.blog.controller;

import com.example.blog.dto.AuthenticationResponse;
import com.example.blog.dto.UserLoginRequest;
import com.example.blog.dto.UserRegisterRequest;
import com.example.blog.security.JwtService;
import com.example.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    private final String jwtToken = "token";

    private static final String END_POINT_PATH = "/api/v1/user";

    @Test
    void register() throws Exception {
        // Given
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .firstName("John")
                .lastName("Evans")
                .email("john_evans@gmail.com")
                .password("Test.123")
                .repeatPassword("Test.123")
                .build();

        AuthenticationResponse response = new AuthenticationResponse(jwtToken);

        // When
        when(userService.register(any(UserRegisterRequest.class))).thenReturn(response);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }

    @Test
    void login() throws Exception {
        // Given
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .email("john_evans@gmail.com")
                .password("Test.123")
                .build();

        AuthenticationResponse response = new AuthenticationResponse(jwtToken);

        // When
        when(userService.login(loginRequest)).thenReturn(response);

        // Then
        mockMvc.perform(post(END_POINT_PATH + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }
}