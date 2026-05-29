package org.blog.services;

import org.blog.models.Comment;
import org.blog.repository.CommentRepository;
import org.blog.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentService commentService;

    @Test
    void create_shouldSaveCommentAndIncrementCount() {
        Comment comment = new Comment(1L, "Great post!", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentRepository.save("Great post!", 10L)).thenReturn(comment);
        when(postRepository.incCountComment(10L)).thenReturn(3);

        Comment result = commentService.create("Great post!", 10L);

        assertEquals("Great post!", result.getText());
        verify(commentRepository).save("Great post!", 10L);
        verify(postRepository).incCountComment(10L);
    }

    @Test
    void delete_shouldDeleteCommentAndDecrementCount() {
        when(commentRepository.delete(10L, 1L)).thenReturn(1);
        when(postRepository.decCountComment(10L)).thenReturn(2);

        boolean result = commentService.delete(10L, 1L);

        assertTrue(result);
        verify(commentRepository).delete(10L, 1L);
        verify(postRepository).decCountComment(10L);
    }

    @Test
    void delete_shouldReturnFalseWhenCommentNotFound() {
        when(commentRepository.delete(10L, 999L)).thenReturn(0);

        boolean result = commentService.delete(10L, 999L);

        assertFalse(result);
        verify(commentRepository).delete(10L, 999L);
        verify(postRepository, never()).decCountComment(anyLong());
    }

    @Test
    void getAllByPostId_shouldReturnComments() {
        Comment comment1 = new Comment(1L, "First", 10L, new Timestamp(System.currentTimeMillis()));
        Comment comment2 = new Comment(2L, "Second", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentRepository.getCommentsByPostId(10L)).thenReturn(List.of(comment1, comment2));

        List<Comment> result = commentService.getAllByPostId(10L);

        assertEquals(2, result.size());
        verify(commentRepository).getCommentsByPostId(10L);
    }

    @Test
    void getByPostIdAndCommentId_shouldReturnComment() {
        Comment comment = new Comment(1L, "My comment", 10L, new Timestamp(System.currentTimeMillis()));
        when(commentRepository.getCommentByPostIdAndCommentId(10L, 1L)).thenReturn(comment);

        Comment result = commentService.getByPostIdAndCommentId(10L, 1L);

        assertNotNull(result);
        assertEquals("My comment", result.getText());
    }

    @Test
    void update_shouldReturnTrueWhenUpdated() {
        when(commentRepository.update(10L, "Updated text", 1L)).thenReturn(1);

        boolean result = commentService.update(10L, "Updated text", 1L);

        assertTrue(result);
        verify(commentRepository).update(10L, "Updated text", 1L);
    }

    @Test
    void update_shouldReturnFalseWhenNotUpdated() {
        when(commentRepository.update(10L, "Updated text", 999L)).thenReturn(0);

        boolean result = commentService.update(10L, "Updated text", 999L);

        assertFalse(result);
    }
}
