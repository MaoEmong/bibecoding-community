package com.example.bibecoding01.comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    List<Comment> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    void delete(Comment comment);
}
