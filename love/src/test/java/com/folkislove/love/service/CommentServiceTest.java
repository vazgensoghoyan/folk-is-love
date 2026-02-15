package com.folkislove.love.service;

import com.folkislove.love.dto.response.CommentResponse;
import com.folkislove.love.mapper.CommentMapper;
import com.folkislove.love.model.Comment;
import com.folkislove.love.model.Post;
import com.folkislove.love.model.User;
import com.folkislove.love.repository.CommentRepository;
import com.folkislove.love.repository.PostRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    private PostRepository postRepository;
    private CommentRepository commentRepository;
    private CurrentUserService currentUserService;
    private CommentMapper commentMapper;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        commentRepository = mock(CommentRepository.class);
        currentUserService = mock(CurrentUserService.class);
        commentMapper = mock(CommentMapper.class);

        commentService = new CommentService(
            commentRepository,
            postRepository,
            currentUserService,
            commentMapper
        );
    }

    @Nested
    class GetCommentsByPostIdTests {

        @Test
        void shouldReturnMappedComments() {
            Long postId = 1L;

            Comment comment1 = Comment.builder().id(1L).content("Hi").build();
            Comment comment2 = Comment.builder().id(2L).content("Hello").build();

            Post post = Post.builder()
                .id(postId)
                .comments(Set.of(comment1, comment2))
                .build();

            CommentResponse dto1 = CommentResponse.builder().id(1L).content("Hi").build();
            CommentResponse dto2 = CommentResponse.builder().id(2L).content("Hello").build();

            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentMapper.toDto(comment1)).thenReturn(dto1);
            when(commentMapper.toDto(comment2)).thenReturn(dto2);

            List<CommentResponse> result = commentService.getCommentsByPostId(postId);

            assertEquals(2, result.size());
            assertTrue(result.contains(dto1));
            assertTrue(result.contains(dto2));
        }

        @Test
        void shouldThrowIfPostNotFound() {
            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commentService.getCommentsByPostId(99L));

            assertEquals("Post not found", ex.getMessage());
        }
    }

    @Nested
    class AddCommentTests {

        @Test
        void shouldCreateAndReturnComment() {
            Long postId = 1L;
            String content = "Nice post!";

            User user = User.builder().username("user1").build();
            Post post = Post.builder().id(postId).build();

            Comment savedComment = Comment.builder()
                .id(10L)
                .content(content)
                .author(user)
                .post(post)
                .build();

            CommentResponse response = CommentResponse.builder()
                .id(10L)
                .content(content)
                .authorUsername("user1")
                .build();

            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(currentUserService.getCurrentUser()).thenReturn(user);
            when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
            when(commentMapper.toDto(savedComment)).thenReturn(response);

            CommentResponse result = commentService.addComment(postId, content);

            assertEquals(response, result);
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        void shouldThrowIfPostNotFound() {
            when(postRepository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commentService.addComment(1L, "text"));

            assertEquals("Post not found", ex.getMessage());
        }
    }

    @Nested
    class EditCommentTests {

        @Test
        void shouldEditCommentWhenOwner() {
            Long commentId = 5L;
            String newContent = "Updated";

            User author = User.builder().username("user1").build();

            Comment comment = Comment.builder()
                .id(commentId)
                .content("Old")
                .author(author)
                .build();

            Comment updated = Comment.builder()
                .id(commentId)
                .content(newContent)
                .author(author)
                .build();

            CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .content(newContent)
                .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doNothing().when(currentUserService).checkIsOwnerOrAdmin("user1");
            when(commentRepository.save(comment)).thenReturn(updated);
            when(commentMapper.toDto(updated)).thenReturn(response);

            CommentResponse result = commentService.editComment(commentId, newContent);

            assertEquals(response, result);
            assertEquals(newContent, comment.getContent());
        }

        @Test
        void shouldThrowIfCommentNotFound() {
            when(commentRepository.findById(1L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commentService.editComment(1L, "text"));

            assertEquals("Comment not found: 1", ex.getMessage());
        }
    }

    @Nested
    class DeleteCommentTests {

        @Test
        void shouldDeleteCommentWhenAuthorized() {
            Long commentId = 3L;
            User author = User.builder().username("user1").build();

            Comment comment = Comment.builder()
                .id(commentId)
                .author(author)
                .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doNothing().when(currentUserService).checkIsOwnerOrAdmin("user1");

            commentService.deleteComment(commentId);

            verify(commentRepository).delete(comment);
        }

        @Test
        void shouldThrowIfCommentNotFound() {
            when(commentRepository.findById(7L)).thenReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> commentService.deleteComment(7L));

            assertEquals("Comment not found: 7", ex.getMessage());
        }
    }
}
