package org.blog.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.annotation.ApplicationScope;

import java.util.Set;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private Long id;
    private String title;
    private String text;
    private Set<String> tags;
    private Integer likesCount;
    private Integer commentsCount;
    private String imagePath;
    private Timestamp createAt;
}


