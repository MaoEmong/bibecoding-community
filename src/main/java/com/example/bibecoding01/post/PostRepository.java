package com.example.bibecoding01.post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post save(Post post);

    Optional<Post> findById(Long id);

    List<Post> findPage(PostRequest.ListDTO condition);

    long count(PostRequest.ListDTO condition);

    void delete(Post post);
}
