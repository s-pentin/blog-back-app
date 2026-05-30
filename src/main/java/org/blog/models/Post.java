package org.blog.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JsonIgnore
    private String imagePath;
    @JsonIgnore
    private Timestamp createdAt;
}


