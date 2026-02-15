package com.folkislove.love.controller;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.service.CommentService;
import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CurrentUserService currentUserService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPost(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody String content
    ) {
        CommentResponse comment = commentService.addComment(postId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> editComment(
            @PathVariable Long commentId,
            @RequestBody String content
    ) {
        String authorUsername = getUsernameByCommendId(commentId);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        CommentResponse updated = commentService.editComment(commentId, content);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        String authorUsername = getUsernameByCommendId(commentId);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // private helper

    private String getUsernameByCommendId(Long id) {
        return commentService.getById(id).getAuthorUsername();
    }
}
