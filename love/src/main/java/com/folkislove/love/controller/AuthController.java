package com.folkislove.love.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.folkislove.love.dto.AuthRequest;
import com.folkislove.love.dto.AuthResponse;
import com.folkislove.love.dto.RegisterRequest;
import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.mapper.UserMapper;
import com.folkislove.love.model.User;
import com.folkislove.love.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        String token = authService.login(
            request.getUsername(), 
            request.getPassword()
        );
        var response = AuthResponse.builder().token(token).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
