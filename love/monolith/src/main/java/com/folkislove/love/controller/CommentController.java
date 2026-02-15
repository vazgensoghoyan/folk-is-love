package com.folkislove.love.controller;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.mapper.CommentMapper;
import com.folkislove.love.model.Comment;
import com.folkislove.love.service.CommentService;
import com.folkislove.love.service.CurrentUserService;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final CurrentUserService currentUserService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
        @PathVariable Long postId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<CommentResponse> comments = commentService
            .getCommentsByPostId(postId, PageRequest.of(page, size))
            .map(commentMapper::toDto);
    
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> addComment(
        @PathVariable Long postId,
        @RequestBody String content
    ) {
        Comment comment = commentService.addComment(postId, content);
        CommentResponse response = commentMapper.toDto(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> editComment(
        @PathVariable Long commentId,
        @RequestBody String content
    ) {
        String authorUsername = getUsernameByCommendId(commentId);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        Comment updated = commentService.editComment(commentId, content);
        CommentResponse response = commentMapper.toDto(updated);
        return ResponseEntity.ok(response);
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
        return commentService.getCommentById(id).getAuthor().getUsername();
    }
}
