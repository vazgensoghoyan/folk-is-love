package com.folkislove.love.controller;

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

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PostMapping("/interests/{tagId}")
    public ResponseEntity<Void> addInterest(@PathVariable Long tagId) {
        userService.addInterest(tagId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/interests/{tagId}")
    public ResponseEntity<Void> removeInterest(@PathVariable Long tagId) {
        userService.removeInterest(tagId);
        return ResponseEntity.ok().build();
    }
}
