package org.blog.controllers;

import org.blog.models.Post;
import org.blog.models.request.PostRequest;
import org.blog.models.Posts;
import org.blog.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@RestController
public class PostController {

    private final PostService postService;
    private final Path imagesStoragePath;

    @Autowired
    public PostController(PostService postService, Path imagesStoragePath) {
        this.postService = postService;
        this.imagesStoragePath = imagesStoragePath;
    }

    @GetMapping("/api/posts")
    public ResponseEntity<Posts> getPosts(
            @RequestParam String search,
            @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        return ResponseEntity.ok(postService.getAll(search, pageNumber, pageSize));
    }

    @GetMapping("/api/posts/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        String imagePath = postService.getImagePath(id);
        if (imagePath == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] image = postService.getImageBytes(id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PostMapping("/api/posts/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PostMapping("/api/posts")
    public ResponseEntity<Post> addPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.create(request.title(), request.text(), request.tags()));
    }

    @PutMapping("/api/posts/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.update(id, request.title(), request.text(), request.tags()));
    }

    @DeleteMapping("/api/posts/{id}")
    public ResponseEntity<Void> deletePostById(@PathVariable Long id) {
        postService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/posts/{id}/likes")
    public ResponseEntity<Integer> addLikeToPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.incrementLikes(id));
    }

    @PutMapping("/api/posts/{id}/image")
    public ResponseEntity<Void> updateImage(@PathVariable Long id,
                                            @RequestParam("image") MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg";
        String filename = id + "_" + System.currentTimeMillis() + extension;
        Path fullPath = imagesStoragePath.resolve(filename);
        file.transferTo(fullPath);
        postService.updateImage(id, fullPath.toString());
        return ResponseEntity.ok().build();
    }
}
