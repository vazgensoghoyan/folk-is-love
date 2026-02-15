package com.folkislove.love.service;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.mapper.CommentMapper;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.Post;
import com.folkislove.love.repository.CommentRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        Post post = postService.getPostById(postId);

        return post.getComments().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentResponse getById(Long eventId) {
        return commentMapper.toDto(findCommentById(eventId));
    }

    @Transactional
    public CommentResponse addComment(Long postId, String content) {
        Post post = postService.getPostById(postId);

        Comment comment = Comment.builder()
                .post(post)
                .author(currentUserService.getCurrentUser())
                .content(content)
                .build();

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Transactional
    public CommentResponse editComment(Long commentId, String content) {
        Comment comment = findCommentById(commentId);

        currentUserService.checkIsOwnerOrAdmin(comment.getAuthor().getUsername());

        comment.setContent(content);
        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = findCommentById(commentId);

        currentUserService.checkIsOwnerOrAdmin(comment.getAuthor().getUsername());

        commentRepository.delete(comment);
    }
}
