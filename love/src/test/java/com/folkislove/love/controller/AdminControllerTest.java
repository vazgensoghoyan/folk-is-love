package com.folkislove.love.controller;

import com.folkislove.love.exception.GlobalExceptionHandler;
import com.folkislove.love.service.CommentService;
import com.folkislove.love.service.CurrentUserService;
import com.folkislove.love.service.admin.UserServiceAdmin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AdminControllerTest {

    private MockMvc mockMvc;
    private CommentService commentService;
    private UserServiceAdmin userServiceAdmin;
    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        commentService = mock(CommentService.class);
        userServiceAdmin = mock(UserServiceAdmin.class);
        currentUserService = mock(CurrentUserService.class);

        AdminController controller = new AdminController(commentService, userServiceAdmin, currentUserService);
        
        mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    @Test
    void deleteComment_shouldReturnNoContent_whenAdmin() throws Exception {
        when(currentUserService.isAdmin()).thenReturn(true);

        mockMvc.perform(delete("/api/admin/comments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L);
    }

    @Test
    void deleteComment_shouldReturnBadRequest_whenNotAdmin() throws Exception {
        when(currentUserService.isAdmin()).thenReturn(false);

        mockMvc.perform(delete("/api/admin/comments/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("You don't have permission to access this resource"));

        verify(commentService, never()).deleteComment(anyLong());
    }

    @Test
    void deleteUser_shouldCallUserService_whenAdmin() throws Exception {
        when(currentUserService.isAdmin()).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(userServiceAdmin, times(1)).deleteUser(2L);
    }

}
