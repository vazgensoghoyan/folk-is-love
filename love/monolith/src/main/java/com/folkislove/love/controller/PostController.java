package com.folkislove.love.controller;

import com.folkislove.common.dto.request.PostRequest;
import com.folkislove.common.dto.response.PostResponse;
import com.folkislove.love.mapper.PostMapper;
import com.folkislove.love.model.Post;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.PostService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostResponse> posts = postService
            .getAllPosts(PageRequest.of(page, size))
            .map(postMapper::toDto);
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
        @Valid @RequestBody PostRequest request
    ) {
        Post post = postService.createPost(request);
        PostResponse response = postMapper.toDto(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        PostResponse response = postMapper.toDto(post);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> editPost(
        @PathVariable Long id,
        @Valid @RequestBody PostRequest request
    ) {
        String authorUsername = getAuthorUsernameByPostId(id);

        currentUserService.checkIsOwner(authorUsername);

        Post updated = postService.editPost(id, request);
        PostResponse response = postMapper.toDto(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String authorUsername = getAuthorUsernameByPostId(id);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<PostResponse>> getPostsByTag(
        @PathVariable Long tagId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<PostResponse> posts = postService
            .getPostsByTag(tagId, PageRequest.of(page, size))
            .map(postMapper::toDto);
        return ResponseEntity.ok(posts);
    }

    // private helper

    private String getAuthorUsernameByPostId(Long id) {
        return postService.getPostById(id).getAuthor().getUsername();
    }
}
