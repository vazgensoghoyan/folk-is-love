package com.folkislove.love.controller;

import com.folkislove.love.dto.request.AuthRequest;
import com.folkislove.love.dto.request.RegisterRequest;
import com.folkislove.love.dto.response.UserResponse;
import com.folkislove.love.mapper.UserMapper;
import com.folkislove.love.model.User;
import com.folkislove.love.service.AuthService;
import com.folkislove.love.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private UserMapper userMapper;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        userMapper = mock(UserMapper.class);
        objectMapper = new ObjectMapper();

        AuthController controller = new AuthController(authService, userMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() throws Exception {
        String token = "jwt-token";
        when(authService.login(anyString(), anyString())).thenReturn(token);

        AuthRequest request = new AuthRequest();
        request.setUsername("user1");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));

        verify(authService, times(1)).login("user1", "password");
    }

    @Test
    void login_shouldReturnBadRequest_whenMissingUsername() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnUserResponse_whenValid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setEmail("user1@example.com");
        request.setPassword("password");

        User user = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();

        UserResponse userResponse = UserResponse.builder()
            .username("user1")
            .email("user1@example.com")
            .build();

        when(authService.register(anyString(), anyString(), anyString())).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@example.com"));

        verify(authService, times(1)).register("user1", "user1@example.com", "password");
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void register_shouldReturnBadRequest_whenMissingEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
