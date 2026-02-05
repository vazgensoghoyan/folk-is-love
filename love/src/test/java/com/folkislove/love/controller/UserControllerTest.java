package com.folkislove.love.controller;

import com.folkislove.love.model.User.Role;
import com.folkislove.love.service.UserService;
import com.folkislove.love.dto.response.UserResponse;
import com.folkislove.love.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);

        UserController controller = new UserController(userService);

        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
    }

    @Test
    void getCurrentUser_shouldReturnUserResponse() throws Exception {
        UserResponse user = UserResponse.builder()
            .username("john")
            .email("john@example.com")
            .bio("Bio")
            .role(Role.USER)
            .interests(List.of("Music", "Art"))
            .build();

        when(userService.getCurrentUser()).thenReturn(user);

        mockMvc.perform(get("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("john"))
            .andExpect(jsonPath("$.email").value("john@example.com"))
            .andExpect(jsonPath("$.bio").value("Bio"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.interests[0]").value("Music"))
            .andExpect(jsonPath("$.interests[1]").value("Art"));

        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    void getUserByUsername_shouldReturnUserResponse() throws Exception {
        UserResponse user = UserResponse.builder()
            .username("alice")
            .email("alice@example.com")
            .role(Role.ADMIN)
            .build();

        when(userService.getUserByUsername("alice")).thenReturn(user);

        mockMvc.perform(get("/api/users/alice")
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.role").value("ADMIN"));

        verify(userService, times(1)).getUserByUsername("alice");
    }

    @Test
    void addInterest_shouldCallService() throws Exception {
        mockMvc.perform(post("/api/users/interests/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).addInterest(5L);
    }

    @Test
    void removeInterest_shouldCallService() throws Exception {
        mockMvc.perform(delete("/api/users/interests/5")
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(userService, times(1)).removeInterest(5L);
    }
}
