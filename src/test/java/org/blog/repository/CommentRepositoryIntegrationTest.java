package org.blog.repository;

import org.blog.config.BaseIntegrationTest;
import org.blog.config.TestAppConfig;
import org.blog.models.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {TestAppConfig.class})
class CommentRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentRepository commentRepository;

    private Long postId;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
        jdbcTemplate.update(
                "INSERT INTO posts (title, text, tags, likes_count, comments_count) VALUES (?,?,?,?,?)",
                "Test Post", "Text", null, 0, 0
        );
        postId = jdbcTemplate.queryForObject(
                "SELECT id FROM posts WHERE title = 'Test Post'", Long.class);
    }

    @Test
    void save_shouldReturnComment() {
        Comment saved = commentRepository.save("Hello", postId);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Hello", saved.getText());
        assertEquals(postId, saved.getPostId());
    }

    @Test
    void getCommentsByPostId_shouldReturnComments() {
        commentRepository.save("First", postId);
        commentRepository.save("Second", postId);

        List<Comment> comments = commentRepository.getCommentsByPostId(postId);

        assertEquals(2, comments.size());
    }

    @Test
    void getCommentByPostIdAndCommentId_shouldReturnComment() {
        Comment saved = commentRepository.save("My comment", postId);

        Comment found = commentRepository.getCommentByPostIdAndCommentId(postId, saved.getId());

        assertNotNull(found);
        assertEquals("My comment", found.getText());
        assertEquals(postId, found.getPostId());
    }

    @Test
    void update_shouldModifyComment() {
        Comment saved = commentRepository.save("Old text", postId);

        int updated = commentRepository.update(postId, "New text", saved.getId());

        assertEquals(1, updated);

        Comment found = commentRepository.getCommentByPostIdAndCommentId(postId, saved.getId());
        assertEquals("New text", found.getText());
    }

    @Test
    void delete_shouldRemoveComment() {
        Comment saved = commentRepository.save("To delete", postId);

        int deleted = commentRepository.delete(postId, saved.getId());

        assertEquals(1, deleted);
        assertTrue(commentRepository.getCommentsByPostId(postId).isEmpty());
    }
}
