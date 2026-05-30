package org.blog.repository;

import org.blog.models.Comment;
import org.blog.models.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import java.util.Set;


@Repository
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Post> getAll() {
        return jdbcTemplate.query(
                "SELECT id, title, text, tags, likes_count, comments_count, image_path, created_at FROM posts ORDER BY id",
                this::mapPostRow
        );
    }

    public List<Post> getAllWithPaginationAndSearch(String search, int pageNumber, int pageSize) {
        return jdbcTemplate.query(
                """
                        SELECT id, title, text, tags, likes_count, comments_count, image_path, created_at FROM posts
                        WHERE title ILIKE ? ORDER BY id LIMIT ? OFFSET ?
                    """,
                this::mapPostRow,
                "%" + search + "%",
                pageSize,
                (pageNumber - 1) * pageSize
        );
    }

    public Integer getTotalPostsCount(String search) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM posts WHERE title ILIKE ?",
                Integer.class,
                "%" + search + "%"
        );
    }

    public Post getPostById(Long id) {
        return jdbcTemplate.queryForObject(
                "SELECT id, title, text, tags, likes_count, comments_count, image_path, created_at FROM posts WHERE id = ?",
                this::mapPostRow,
                id
        );
    }

    public Post save(String title, String text, Set<String> tags) {
        return jdbcTemplate.execute((Connection con) -> {
            Array sqlTags = con.createArrayOf("text", tags.toArray(new String[0]));
            var ps = con.prepareStatement("INSERT INTO posts (title, text, tags) VALUES (?, ?, ?) RETURNING *");
            ps.setString(1, title);
            ps.setString(2, text);
            ps.setArray(3, sqlTags);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return mapPostRow(rs, 0);
        });
    }

    public Post update(Long id, String title, String text, Set<String> tags) {
        return jdbcTemplate.execute((Connection con) -> {
            Array sqlTags = con.createArrayOf("text", tags.toArray(new String[0]));
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE posts SET title = ?, text = ?, tags = ? WHERE id = ? RETURNING *");
            ps.setString(1, title);
            ps.setString(2, text);
            ps.setArray(3, sqlTags);
            ps.setLong(4, id);

            ResultSet rs = ps.executeQuery();
            rs.next();
            return mapPostRow(rs, 0);
        });
    }

    public int delete(Long id) {
        return jdbcTemplate.update("DELETE FROM posts WHERE id = ?", id);

    }

    public Integer incCountLikes(Long id) {
        return jdbcTemplate.queryForObject(
                "UPDATE posts SET likes_count = likes_count + 1 WHERE id = ? RETURNING likes_count",
                Integer.class,
                id
        );
    }

    public Integer incCountComment(Long postId) {
        return jdbcTemplate.queryForObject(
                "UPDATE posts SET comments_count = comments_count + 1 WHERE id = ? RETURNING comments_count",
                Integer.class,
                postId
        );
    }

    public Integer decCountComment(Long postId) {
        return jdbcTemplate.queryForObject(
                "UPDATE posts SET comments_count = comments_count - 1 WHERE id = ? RETURNING comments_count",
                Integer.class,
                postId
        );
    }

    public int updateImage(Long id, String imagePath) {
        return jdbcTemplate.update("UPDATE posts SET image_path = ? WHERE id = ?", imagePath, id);
    }

    public String getImagePath(Long id) {
        return jdbcTemplate.queryForObject("SELECT image_path FROM posts WHERE id = ?", String.class, id);
    }

    private Post mapPostRow(ResultSet rs, int rowNum) throws SQLException {
        Post post = new Post();
        post.setId(rs.getLong("id"));
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));

        Array tagsArray = rs.getArray("tags");
        if (tagsArray != null) {
            post.setTags(Set.of((String[]) tagsArray.getArray()));
        } else {
            post.setTags(Set.of());
        }

        post.setLikesCount(rs.getInt("likes_count"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setImagePath(rs.getString("image_path"));
        post.setCreatedAt(rs.getTimestamp("created_at"));

        return post;
    }
}
