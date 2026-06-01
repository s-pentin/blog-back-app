package org.blog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blog.models.Comment;
import org.blog.models.request.CommentRequest;
import org.blog.services.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        CommentController controller = new CommentController(commentService);
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    void getAllComments_shouldReturn200() throws Exception {
        Comment comment = new Comment(1L, "Nice!", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentService.getAllByPostId(10L)).thenReturn(List.of(comment));

        mockMvc.perform(get("/api/posts/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Nice!"));
    }

    @Test
    void getComment_shouldReturn200() throws Exception {
        Comment comment = new Comment(1L, "My comment", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentService.getByPostIdAndCommentId(10L, 1L)).thenReturn(comment);

        mockMvc.perform(get("/api/posts/10/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("My comment"));
    }

    @Test
    void createComment_shouldReturn200() throws Exception {
        Comment created = new Comment(1L, "New comment", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentService.create(eq("New comment"), eq(10L))).thenReturn(created);

        CommentRequest request = new CommentRequest(null, "New comment", 10L);

        mockMvc.perform(post("/api/posts/10/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New comment"));
    }

    @Test
    void updateComment_shouldReturn200() throws Exception {
        Comment updated = new Comment(1L, "Updated text", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentService.update(eq(10L), eq("Updated text"), eq(1L))).thenReturn(true);
        when(commentService.getByPostIdAndCommentId(10L, 1L)).thenReturn(updated);

        CommentRequest request = new CommentRequest(1L, "Updated text", 10L);

        mockMvc.perform(put("/api/posts/10/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated text"));
    }

    @Test
    void deleteComment_shouldReturn200() throws Exception {
        when(commentService.delete(10L, 1L)).thenReturn(true);

        mockMvc.perform(delete("/api/posts/10/comments/1"))
                .andExpect(status().isOk());
    }
}
