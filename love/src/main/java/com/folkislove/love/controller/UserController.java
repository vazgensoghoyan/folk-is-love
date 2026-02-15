package com.folkislove.love.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.folkislove.love.dto.response.UserResponse;
import com.folkislove.love.mapper.UserMapper;
import com.folkislove.love.model.User;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        User current = currentUserService.getCurrentUser();
        UserResponse response = userMapper.toDto(current);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        User current = userService.getUserByUsername(username);
        UserResponse response = userMapper.toDto(current);
        return ResponseEntity.ok(response);
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

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        currentUserService.checkIsAdmin();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
