package org.blog.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private Long id;
    private String text;
    private Long postId;
    @JsonIgnore
    private Timestamp createAt;

}
