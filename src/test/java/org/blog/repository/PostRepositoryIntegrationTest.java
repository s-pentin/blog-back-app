package org.blog.repository;

import org.blog.config.BaseIntegrationTest;
import org.blog.config.TestAppConfig;
import org.blog.models.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {TestAppConfig.class})
class PostRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM comments");
        jdbcTemplate.execute("DELETE FROM posts");
    }

    @Test
    void save_shouldReturnPost() {
        Post saved = postRepository.save("Title", "Text", Set.of("java", "blog"));

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Title", saved.getTitle());
        assertEquals(Set.of("java", "blog"), saved.getTags());
    }

    @Test
    void getPostById_shouldReturnPost() {
        Post saved = postRepository.save("Title", "Text", Set.of("tag"));

        Post found = postRepository.getPostById(saved.getId());

        assertNotNull(found);
        assertEquals("Title", found.getTitle());
        assertEquals("Text", found.getText());
    }

    @Test
    void getAll_shouldReturnAllPosts() {
        postRepository.save("First", "Text 1", Set.of("a"));
        postRepository.save("Second", "Text 2", Set.of("b"));

        List<Post> posts = postRepository.getAll();

        assertEquals(2, posts.size());
    }

    @Test
    void delete_shouldRemovePost() {
        Post saved = postRepository.save("Title", "Text", Set.of());

        int deleted = postRepository.delete(saved.getId());

        assertEquals(1, deleted);
        assertTrue(postRepository.getAll().isEmpty());
    }

    @Test
    void updateImage_shouldSetPath() {
        Post saved = postRepository.save("Title", "Text", Set.of());

        int result = postRepository.updateImage(saved.getId(), "/images/test.jpg");

        assertEquals(1, result);
        assertEquals("/images/test.jpg", postRepository.getImagePath(saved.getId()));
    }

    @Test
    void getImagePath_shouldReturnPath() {
        Post saved = postRepository.save("Title", "Text", Set.of());
        jdbcTemplate.update(
                "UPDATE posts SET image_path = ? WHERE id = ?", "/img/pic.png", saved.getId());

        String path = postRepository.getImagePath(saved.getId());

        assertEquals("/img/pic.png", path);
    }
}
