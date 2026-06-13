package org.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class AppConfig {

    @Value("${images.storage.path}")
    private String imagesStoragePath;

    @Bean
    public Path imagesStoragePath() throws IOException {
        Path path = Path.of(imagesStoragePath);
        Files.createDirectories(path);
        return path;
    }
}