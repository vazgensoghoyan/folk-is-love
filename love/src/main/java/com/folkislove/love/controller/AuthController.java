package com.folkislove.love.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.folkislove.love.dto.request.AuthRequest;
import com.folkislove.love.dto.request.RegisterRequest;
import com.folkislove.love.dto.response.AuthResponse;
import com.folkislove.love.dto.response.UserResponse;
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
        String token = authService.login(request);
        var response = AuthResponse.builder().token(token).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(user));
    }
}
