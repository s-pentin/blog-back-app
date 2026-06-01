package org.blog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blog.config.BaseIntegrationTest;
import org.blog.config.TestAppConfig;
import org.blog.config.TestWebConfig;
import org.blog.models.request.CommentRequest;
import org.blog.repository.CommentRepository;
import org.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebAppConfiguration
@ContextConfiguration(classes = {TestAppConfig.class, TestWebConfig.class})
class CommentControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private Long postId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        var post = postRepository.save("Test Post", "Text", java.util.Set.of());
        postId = post.getId();
    }

    @Test
    void createComment_shouldPersistAndReturn() throws Exception {
        CommentRequest request = new CommentRequest(null, "Great post!", postId);

        mockMvc.perform(post("/api/posts/{postId}/comments", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great post!"))
                .andExpect(jsonPath("$.postId").value(postId));
    }

    @Test
    void getAllComments_shouldReturnList() throws Exception {
        commentRepository.save("First", postId);
        commentRepository.save("Second", postId);

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getComment_shouldReturnComment() throws Exception {
        var saved = commentRepository.save("My comment", postId);

        mockMvc.perform(get("/api/posts/{postId}/comments/{commentId}", postId, saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("My comment"))
                .andExpect(jsonPath("$.id").value(saved.getId()));
    }

    @Test
    void updateComment_shouldModifyAndReturn() throws Exception {
        var saved = commentRepository.save("Old", postId);
        CommentRequest request = new CommentRequest(saved.getId(), "Updated text", postId);

        mockMvc.perform(put("/api/posts/{postId}/comments/{commentId}", postId, saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated text"));
    }

    @Test
    void deleteComment_shouldRemoveComment() throws Exception {
        var saved = commentRepository.save("To delete", postId);

        mockMvc.perform(delete("/api/posts/{postId}/comments/{commentId}", postId, saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/posts/{postId}/comments", postId))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
