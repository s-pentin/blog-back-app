package org.blog.services;

import org.blog.models.Post;
import org.blog.models.Posts;
import org.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;

    private final int MAX_LENGTH_TEXT = 128;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public Posts getAll(String search, Integer pageNumber, Integer pageSize) {
        List<Post> posts = postRepository.getAllWithPaginationAndSearch(search, pageNumber, pageSize);
        Integer totalCount = postRepository.getTotalPostsCount(search);
        Integer lastPage = (int) Math.ceil((double) totalCount / pageSize);

        for (Post post: posts) {
            if (post.getText().length() > MAX_LENGTH_TEXT) {
                post.setText(post.getText().substring(0, MAX_LENGTH_TEXT) + "...");
            }
        }

        return new Posts(posts, pageNumber > 1, pageNumber < lastPage, lastPage);
    }

    public Post getById(Long id) {
        return postRepository.getPostById(id);
    }

    public Post create(String title, String text, Set<String> tags) {
        return postRepository.save(title, text, tags);
    }

    public Post update(Long id, String title, String text, Set<String> tags) {
        return postRepository.update(id, title, text, tags);
    }

    public boolean delete(Long id) {
       return postRepository.delete(id) == 1;
    }

    public Integer incrementLikes(Long id) {
        return postRepository.incCountLikes(id);
    }

    public boolean updateImage(Long id, String imagePath) {
        return postRepository.updateImage(id, imagePath) == 1;
    }

    public String getImagePath(Long id) {
        return postRepository.getImagePath(id);
    }

    public byte[] getImageBytes(Long id) {
        String imagePath = postRepository.getImagePath(id);
        try {
            return Files.readAllBytes(Path.of(imagePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image", e);
        }
    }
}
