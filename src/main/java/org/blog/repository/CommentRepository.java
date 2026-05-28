package org.blog.repository;

import org.blog.models.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(String text, Long postId) {
        return jdbcTemplate.queryForObject(
                "INSERT INTO comments (text, post_id) VALUES (?, ?) RETURNING id",
                Long.class,
                text,
                postId
        );
    }

    public int update(Long postId, String text, Long commentId) {
        return jdbcTemplate.update(
                "UPDATE comments SET text = ? WHERE id = ? AND post_id = ?", text, commentId, postId);
    }

    public int delete(Long postId, Long commentId) {
        return jdbcTemplate.update("DELETE FROM comments WHERE id = ? AND post_id = ?", commentId, postId);
    }

    public List<Comment> getCommentsByPostId(Long postId) {
        return jdbcTemplate.query(
                "SELECT id, text, post_id FROM comments WHERE post_id = ?",
                this::mapRow,
                postId
        );
    }

    public Comment getCommentByPostIdAndCommentId(Long postId, Long commentId) {
        return jdbcTemplate.queryForObject(
                "SELECT id, text, post_id FROM comments WHERE post_id = ? AND id = ?",
                this::mapRow,
                postId,
                commentId
        );
    }

    private Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
        Comment comment = new Comment();
        comment.setId(rs.getLong("id"));
        comment.setText(rs.getString("text"));
        comment.setPostId(rs.getLong("post_id"));
        return comment;
    }
}
