package com.folkislove.love.service;

import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.PostRepository;
import com.folkislove.love.dto.request.PostRequest;
import com.folkislove.love.dto.response.PostResponse;
import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.mapper.PostMapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final TagService tagService;
    private final CurrentUserService currentUserService;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByTag(Long tagId) {
        Tag tag = tagService.getTagById(tagId);

        Set<Post> posts = tag.getPosts();
        return posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", postId));
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
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

        Post saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    @Transactional
    public PostResponse editPost(Long postId, PostRequest request) {
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

        Post saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = getPostById(postId);
        currentUserService.checkIsOwnerOrAdmin(post.getAuthor().getUsername());
        postRepository.delete(post);
    }
}
