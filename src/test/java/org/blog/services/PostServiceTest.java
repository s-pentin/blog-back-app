package org.blog.services;

import org.blog.models.Post;
import org.blog.models.Posts;
import org.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void getAll_shouldTruncateLongText() {
        String longText = "a".repeat(200);
        Post post = new Post(1L, "Title", longText, Set.of("java"), 5, 2,
                null, new Timestamp(System.currentTimeMillis()));

        when(postRepository.getAllWithPaginationAndSearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(post));
        when(postRepository.getTotalPostsCount(anyString())).thenReturn(1);

        Posts result = postService.getAll("", 1, 10);

        assertEquals(128 + 3, result.getPosts().getFirst().getText().length());
        assertTrue(result.getPosts().getFirst().getText().endsWith("..."));
    }

    @Test
    void getAll_shouldNotTruncateShortText() {
        Post post = new Post(1L, "Title", "Short text", Set.of("java"), 5, 2,
                null, new Timestamp(System.currentTimeMillis()));

        when(postRepository.getAllWithPaginationAndSearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(post));
        when(postRepository.getTotalPostsCount(anyString())).thenReturn(1);

        Posts result = postService.getAll("", 1, 10);

        assertEquals("Short text", result.getPosts().getFirst().getText());
    }

    @Test
    void getAll_shouldReturnCorrectPagination_firstPage() {
        when(postRepository.getAllWithPaginationAndSearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());
        when(postRepository.getTotalPostsCount(anyString())).thenReturn(25);

        Posts result = postService.getAll("", 1, 10);

        assertFalse(result.getHasPrev());
        assertTrue(result.getHasNext());
        assertEquals(3, result.getLastPage());
    }

    @Test
    void getAll_shouldReturnCorrectPagination_lastPage() {
        when(postRepository.getAllWithPaginationAndSearch(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());
        when(postRepository.getTotalPostsCount(anyString())).thenReturn(25);

        Posts result = postService.getAll("", 3, 10);

        assertTrue(result.getHasPrev());
        assertFalse(result.getHasNext());
    }

    @Test
    void getById_shouldReturnPost() {
        Post post = new Post(1L, "Title", "Text", Set.of("java"), 5, 2,
                null, new Timestamp(System.currentTimeMillis()));
        when(postRepository.getPostById(1L)).thenReturn(post);

        Post result = postService.getById(1L);

        assertNotNull(result);
        assertEquals("Title", result.getTitle());
        verify(postRepository, times(1)).getPostById(1L);
    }

    @Test
    void create_shouldSavePost() {
        Post created = new Post(1L, "New Title", "New Text", Set.of("java"), 0, 0,
                null, new Timestamp(System.currentTimeMillis()));
        when(postRepository.save(eq("New Title"), eq("New Text"), eq(Set.of("java"))))
                .thenReturn(created);

        Post result = postService.create("New Title", "New Text", Set.of("java"));

        assertEquals("New Title", result.getTitle());
        verify(postRepository, times(1)).save("New Title", "New Text", Set.of("java"));
    }

    @Test
    void delete_shouldReturnTrueWhenDeleted() {
        when(postRepository.delete(1L)).thenReturn(1);

        boolean result = postService.delete(1L);

        assertTrue(result);
        verify(postRepository).delete(1L);
    }

    @Test
    void delete_shouldReturnFalseWhenNotDeleted() {
        when(postRepository.delete(1L)).thenReturn(0);

        boolean result = postService.delete(1L);

        assertFalse(result);
    }

    @Test
    void incrementLikes_shouldReturnNewCount() {
        when(postRepository.incCountLikes(1L)).thenReturn(6);

        Integer result = postService.incrementLikes(1L);

        assertEquals(6, result);
        verify(postRepository).incCountLikes(1L);
    }

    @Test
    void updateImage_shouldReturnTrueWhenUpdated() {
        when(postRepository.updateImage(1L, "/path/to/image.jpg")).thenReturn(1);

        boolean result = postService.updateImage(1L, "/path/to/image.jpg");

        assertTrue(result);
    }

    @Test
    void updateImage_shouldReturnFalseWhenNotUpdated() {
        when(postRepository.updateImage(1L, "/path/to/image.jpg")).thenReturn(0);

        boolean result = postService.updateImage(1L, "/path/to/image.jpg");

        assertFalse(result);
    }
}
