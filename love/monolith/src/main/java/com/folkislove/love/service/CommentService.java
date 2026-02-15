package com.folkislove.love.service;

import com.folkislove.love.exception.custom.ResourceNotFoundException;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.Post;
import com.folkislove.love.repository.CommentRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long eventId) {
        return findCommentById(eventId);
    }

    @Transactional
    public Comment addComment(Long postId, String content) {
        Post post = postService.getPostById(postId);

        Comment comment = Comment.builder()
                .post(post)
                .author(currentUserService.getCurrentUser())
                .content(content)
                .build();

        return commentRepository.save(comment);
    }

    @Transactional
    public Comment editComment(Long commentId, String content) {
        Comment comment = findCommentById(commentId);

        currentUserService.checkIsOwnerOrAdmin(comment.getAuthor().getUsername());

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = findCommentById(commentId);

        currentUserService.checkIsOwnerOrAdmin(comment.getAuthor().getUsername());

        commentRepository.delete(comment);
    }
}
