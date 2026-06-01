package org.blog.services;

import org.blog.models.Comment;
import org.blog.repository.CommentRepository;
import org.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<Comment> getAllByPostId(Long postId) {
        return commentRepository.getCommentsByPostId(postId);
    }

    public Comment getByPostIdAndCommentId(Long postId, Long commentId) {
        return commentRepository.getCommentByPostIdAndCommentId(postId, commentId);
    }

    public Comment create(String text,  Long postId) {
        Comment comment = commentRepository.save(text, postId);
        postRepository.incCountComment(postId);
        return comment;
    }

    public boolean update(Long postId, String text, Long commentId) {
        return commentRepository.update(postId, text, commentId) == 1;
    }

    public boolean delete(Long postId, Long commentId) {
        boolean deleted = commentRepository.delete(postId, commentId) == 1;
        if (deleted) {
            postRepository.decCountComment(postId);
        }
        return deleted;
    }
}
