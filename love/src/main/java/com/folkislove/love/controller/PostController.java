package com.folkislove.love.controller;

import com.folkislove.love.dto.request.PostRequest;
import com.folkislove.love.dto.response.PostResponse;
import com.folkislove.love.mapper.PostMapper;
import com.folkislove.love.model.Post;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.PostService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final CurrentUserService currentUserService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        PostResponse post = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
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
            @RequestBody PostRequest request
    ) {
        String authorUsername = getAuthorUsernameByPostId(id);

        currentUserService.checkIsOwner(authorUsername);

        PostResponse updated = postService.editPost(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        String authorUsername = getAuthorUsernameByPostId(id);

        currentUserService.checkIsOwnerOrAdmin(authorUsername);

        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<PostResponse>> getPostsByTag(@PathVariable Long tagId) {
        List<PostResponse> posts = postService.getPostsByTag(tagId);
        return ResponseEntity.ok(posts);
    }

    // private helper

    private String getAuthorUsernameByPostId(Long id) {
        return postService.getPostById(id).getAuthor().getUsername();
    }
}
