package org.blog.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blog.models.Post;
import org.blog.models.Posts;
import org.blog.models.request.PostRequest;
import org.blog.services.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    private PostService postService;
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        PostController controller = new PostController(postService, java.nio.file.Path.of("/tmp/images"));
        mockMvc = standaloneSetup(controller).build();
    }

    @Test
    void getPosts_shouldReturn200WithPosts() throws Exception {
        Posts posts = new Posts(List.of(), false, false, 1);
        when(postService.getAll(anyString(), anyInt(), anyInt())).thenReturn(posts);

        mockMvc.perform(get("/api/posts")
                        .param("search", "")
                        .param("pageNumber", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastPage").value(1));
    }

    @Test
    void getPostById_shouldReturn200WithPost() throws Exception {
        Post post = new Post(1L, "Title", "Text", Set.of("java"), 5, 2,
                null, new Timestamp(System.currentTimeMillis()));
        when(postService.getById(1L)).thenReturn(post);

        mockMvc.perform(post("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void addPost_shouldReturn200WithCreatedPost() throws Exception {
        Post created = new Post(1L, "New Post", "Content", Set.of("tag"), 0, 0,
                null, new Timestamp(System.currentTimeMillis()));
        when(postService.create(eq("New Post"), eq("Content"), eq(Set.of("tag")))).thenReturn(created);

        PostRequest request = new PostRequest("New Post", "Content", Set.of("tag"));

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Post"));
    }

    @Test
    void updatePost_shouldReturn200WithUpdatedPost() throws Exception {
        Post updated = new Post(1L, "Updated", "New text", Set.of("tag"), 0, 0,
                null, new Timestamp(System.currentTimeMillis()));
        when(postService.update(eq(1L), eq("Updated"), eq("New text"), eq(Set.of("tag")))).thenReturn(updated);

        PostRequest request = new PostRequest("Updated", "New text", Set.of("tag"));

        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deletePostById_shouldReturn200() throws Exception {
        when(postService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isOk());
    }

    @Test
    void addLikeToPost_shouldReturn200WithCount() throws Exception {
        when(postService.incrementLikes(1L)).thenReturn(6);

        mockMvc.perform(post("/api/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(content().string("6"));
    }

    @Test
    void getImage_shouldReturn404WhenNoImage() throws Exception {
        when(postService.getImagePath(1L)).thenReturn(null);

        mockMvc.perform(get("/api/posts/1/image"))
                .andExpect(status().isNotFound());
    }
}
