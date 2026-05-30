package org.blog.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;

@ExtendWith(SpringExtension.class)
public abstract class BaseIntegrationTest {

    static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("blogdb")
                    .withUsername("testuser")
                    .withPassword("testpass");

    static {
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("db.url", postgresContainer::getJdbcUrl);
        registry.add("db.username", postgresContainer::getUsername);
        registry.add("db.password", postgresContainer::getPassword);
        registry.add("db.driver", () -> "org.postgresql.Driver");
        registry.add("images.storage.path", () -> "/tmp/blog-test-images");
    }
}
