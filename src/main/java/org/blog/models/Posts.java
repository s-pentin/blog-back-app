package org.blog.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    private List<Post> posts;
    private Boolean hasPrev;
    private Boolean hasNext;
    private Integer lastPage;
}
