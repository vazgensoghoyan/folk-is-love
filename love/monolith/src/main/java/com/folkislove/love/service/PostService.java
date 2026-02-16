package com.folkislove.love.service;

import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.PostRepository;
import com.folkislove.common.dto.request.PostRequest;
import com.folkislove.love.exception.custom.ResourceNotFoundException;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Post> getPostsByTag(Long tagId, Pageable pageable) {
        return postRepository.findAllByTags_Id(tagId, pageable);
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
    }

    @Transactional
    public Post createPost(PostRequest request) {
        Set<Tag> tags = request
                .getTagIds()
                .stream()
                .map(tagService::getTagById)
                .collect(Collectors.toSet());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(currentUserService.getCurrentUser())
                .tags(tags)
                .build();

        return postRepository.save(post);
    }

    @Transactional
    public Post editPost(Long postId, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));

        currentUserService.checkIsOwnerOrAdmin(post.getAuthor().getUsername());

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());

        if (request.getTagIds() != null) {
            Set<Tag> tags = request
                .getTagIds()
                .stream()
                .map(tagService::getTagById)
                .collect(Collectors.toSet());

            post.setTags(tags);
        }

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        currentUserService.checkIsOwnerOrAdmin(post.getAuthor().getUsername());
        postRepository.delete(post);
    }
}
