package com.example.bibecoding01.comment;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final EntityManager em;

    public CommentRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        List<Comment> result = em.createQuery(
                        "select c from Comment c join fetch c.post p join fetch c.author u where c.id = :id",
                        Comment.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<Comment> findByPostId(Long postId) {
        return em.createQuery(
                        "select c from Comment c join fetch c.author u where c.post.id = :postId order by c.id asc",
                        Comment.class
                )
                .setParameter("postId", postId)
                .getResultList();
    }

    @Override
    public void deleteByPostId(Long postId) {
        List<Comment> comments = em.createQuery(
                        "select c from Comment c where c.post.id = :postId",
                        Comment.class
                )
                .setParameter("postId", postId)
                .getResultList();

        for (Comment comment : comments) {
            em.remove(comment);
        }
    }

    @Override
    public void delete(Comment comment) {
        em.remove(comment);
    }
}
