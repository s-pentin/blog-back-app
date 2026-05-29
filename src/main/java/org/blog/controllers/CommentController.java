package org.blog.controllers;

import org.blog.models.Comment;
import org.blog.models.request.CommentRequest;
import org.blog.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /*
      5. CommentController — все эндпоинты комментариев

  - GET    /api/posts/{postId}/comments — список комментариев поста, возвращает List<Comment> (JSON)
  - GET    /api/posts/{postId}/comments/{commentId} — один комментарий, возвращает Comment (JSON)
  - POST   /api/posts/{postId}/comments — создать комментарий, принимает JSON {text, postId}, возвращает Comment
  - PUT    /api/posts/{postId}/comments/{commentId} — редактировать комментарий, принимает JSON {id, text, postId}, возвращает Comment
  - DELETE /api/posts/{postId}/comments/{commentId} — удалить комментарий, вернуть 200 Ok
*/

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<Comment>> getAllComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getAllByPostId(postId));
    }

    @GetMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> getCommentByPostIdAndCommentId(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getByPostIdAndCommentId(postId, commentId));
    }

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.create(commentRequest.text(), postId));
    }

    @PutMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest commentRequest) {
        commentService.update(postId, commentRequest.text(), commentId);
        return ResponseEntity.ok(commentService.getByPostIdAndCommentId(postId, commentId));
    }

    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId) {
        commentService.delete(postId, commentId);
        return ResponseEntity.ok().build();
    }
}