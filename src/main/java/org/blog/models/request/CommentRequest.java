package org.blog.models.request;

public record CommentRequest(Long id, String text, Long postId) {
}

