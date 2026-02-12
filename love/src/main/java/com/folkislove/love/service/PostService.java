package com.folkislove.love.service;

import com.folkislove.love.model.Post;
import com.folkislove.love.model.Tag;
import com.folkislove.love.repository.PostRepository;
import com.folkislove.love.repository.TagRepository;
import com.folkislove.love.dto.request.PostRequest;
import com.folkislove.love.dto.response.PostResponse;
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
    private final TagRepository tagRepository;
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
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + tagId));

        Set<Post> posts = tag.getPosts();
        return posts.stream()
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return postMapper.toDto(post);
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
        Set<Tag> tags = request
                .getTagIds()
                .stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tag not found: " + id)))
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
                .orElseThrow(() -> new RuntimeException("Post not found"));

        currentUserService.checkOwnerOrAdmin(post.getAuthor().getUsername());

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getContent() != null) post.setContent(request.getContent());

        if (request.getTagIds() != null) {
            Set<Tag> tags = request
                .getTagIds()
                .stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tag not found: " + id)))
                .collect(Collectors.toSet());

            post.setTags(tags);
        }

        Post saved = postRepository.save(post);
        return postMapper.toDto(saved);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        currentUserService.checkOwnerOrAdmin(post.getAuthor().getUsername());

        postRepository.delete(post);
    }
}
