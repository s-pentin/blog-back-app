CREATE TABLE IF NOT EXISTS posts(
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    tags TEXT[],
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    image_path VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments(
    id SERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        CONSTRAINT fk_comments_post
            FOREIGN KEY (post_id)
            REFERENCES posts(id)
            ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comments_post_id ON comments(post_id);