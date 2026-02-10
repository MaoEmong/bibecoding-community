package com.example.bibecoding01.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final EntityManager em;

    public PostRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            em.persist(post);
            return post;
        }
        return em.merge(post);
    }

    @Override
    public Optional<Post> findById(Long id) {
        List<Post> result = em.createQuery(
                        "select p from Post p join fetch p.author where p.id = :id",
                        Post.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<Post> findPage(PostRequest.ListDTO condition) {
        QueryParts parts = createWhereClause(condition);
        String jpql = "select p from Post p join fetch p.author u" + parts.whereClause() + " order by p.id desc";

        TypedQuery<Post> query = em.createQuery(jpql, Post.class)
                .setFirstResult(condition.getPage() * condition.getSize())
                .setMaxResults(condition.getSize());
        applyParameters(query, parts.params());
        return query.getResultList();
    }

    @Override
    public long count(PostRequest.ListDTO condition) {
        QueryParts parts = createWhereClause(condition);
        String jpql = "select count(p) from Post p join p.author u" + parts.whereClause();

        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        applyParameters(query, parts.params());
        return query.getSingleResult();
    }

    @Override
    public void delete(Post post) {
        em.remove(post);
    }

    private QueryParts createWhereClause(PostRequest.ListDTO condition) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(condition.getKeyword())) {
            where.append(" and (p.title like :keyword or p.content like :keyword or u.name like :keyword)");
            params.put("keyword", "%" + condition.getKeyword().trim() + "%");
        }

        return new QueryParts(where.toString(), params);
    }

    private void applyParameters(TypedQuery<?> query, Map<String, Object> params) {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }

    private record QueryParts(String whereClause, Map<String, Object> params) {
    }
}
