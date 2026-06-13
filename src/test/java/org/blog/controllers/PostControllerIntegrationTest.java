package org.blog.controllers;

import org.blog.config.BaseIntegrationTest;
import org.blog.models.Post;
import org.blog.models.request.PostRequest;
import org.blog.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void addPost_shouldCreateAndReturnPost() throws Exception {
        PostRequest request = new PostRequest("My Post", "Post content", Set.of("java", "blog"));

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("My Post"))
                .andExpect(jsonPath("$.text").value("Post content"))
                .andExpect(jsonPath("$.likesCount").value(0));
    }

    @Test
    void getPostById_shouldReturnPost() throws Exception {
        Post saved = postRepository.save("Title", "Text", Set.of("tag"));

        mockMvc.perform(post("/api/posts/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void getPosts_shouldReturnList() throws Exception {
        postRepository.save("First", "Text 1", Set.of("a"));
        postRepository.save("Second", "Text 2", Set.of("b"));

        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(2)));
    }

    @Test
    void updatePost_shouldModifyAndReturn() throws Exception {
        Post saved = postRepository.save("Old Title", "Old text", Set.of("old"));
        PostRequest request = new PostRequest("New Title", "New text", Set.of("new"));

        mockMvc.perform(put("/api/posts/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.text").value("New text"));
    }

    @Test
    void deletePost_shouldRemovePost() throws Exception {
        Post saved = postRepository.save("Title", "Text", Set.of());

        mockMvc.perform(delete("/api/posts/{id}", saved.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void addLike_shouldIncrementLikes() throws Exception {
        Post saved = postRepository.save("Title", "Text", Set.of());

        mockMvc.perform(post("/api/posts/{id}/likes", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void getImage_shouldReturn404WhenNoImage() throws Exception {
        Post saved = postRepository.save("Title", "Text", Set.of());

        mockMvc.perform(get("/api/posts/{id}/image", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
