package com.folkislove.love.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.folkislove.love.dto.UserResponse;
import com.folkislove.love.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    // Получение пользователя по username
    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    // Добавление интереса
    @PostMapping("/interests/{tagId}")
    public ResponseEntity<Void> addInterest(@PathVariable Long tagId) {
        userService.addInterest(tagId);
        return ResponseEntity.ok().build();
    }

    // Удаление интереса
    @DeleteMapping("/interests/{tagId}")
    public ResponseEntity<Void> removeInterest(@PathVariable Long tagId) {
        userService.removeInterest(tagId);
        return ResponseEntity.ok().build();
    }

    // Получение всех пользователей (только админ)
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }
}
