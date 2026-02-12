package com.folkislove.love.controller;

import com.folkislove.love.service.CommentService;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.PostService;
import com.folkislove.love.service.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для админских действий.
 * Доступ только пользователям с ролью ADMIN.
 */
@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        currentUserService.checkIsAdmin();
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/events/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        currentUserService.checkIsAdmin();
        //eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        currentUserService.checkIsAdmin();
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        currentUserService.checkIsAdmin();
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
