package com.example.bibecoding01.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            em.persist(user);
            return user;
        }
        return em.merge(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        List<User> result = em.createQuery(
                        "select u from User u join fetch u.department d join fetch u.position p where u.id = :id",
                        User.class
                )
                .setParameter("id", id)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> result = em.createQuery("select u from User u where u.email = :email", User.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmployeeNo(String employeeNo) {
        List<User> result = em.createQuery("select u from User u where u.employeeNo = :employeeNo", User.class)
                .setParameter("employeeNo", employeeNo)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return em.createQuery(
                        "select u from User u join fetch u.department d join fetch u.position p order by u.id asc",
                        User.class
                )
                .getResultList();
    }

    @Override
    public List<User> findPage(UserQueryCondition condition) {
        QueryParts parts = createWhereClause(condition);
        String jpql = "select u from User u join fetch u.department d join fetch u.position p" + parts.whereClause() + " order by u.id desc";

        TypedQuery<User> query = em.createQuery(jpql, User.class)
                .setFirstResult(condition.getPage() * condition.getSize())
                .setMaxResults(condition.getSize());

        applyParameters(query, parts.params());
        return query.getResultList();
    }

    @Override
    public long count(UserQueryCondition condition) {
        QueryParts parts = createWhereClause(condition);
        String jpql = "select count(u) from User u join u.department d join u.position p" + parts.whereClause();
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        applyParameters(query, parts.params());
        return query.getSingleResult();
    }

    @Override
    public void delete(User user) {
        em.remove(user);
    }

    private QueryParts createWhereClause(UserQueryCondition condition) {
        StringBuilder where = new StringBuilder(" where 1=1");
        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(condition.getEmployeeNo())) {
            where.append(" and u.employeeNo like :employeeNo");
            params.put("employeeNo", "%" + condition.getEmployeeNo().trim() + "%");
        }
        if (StringUtils.hasText(condition.getName())) {
            where.append(" and u.name like :name");
            params.put("name", "%" + condition.getName().trim() + "%");
        }
        if (condition.getDepartmentId() != null) {
            where.append(" and d.id = :departmentId");
            params.put("departmentId", condition.getDepartmentId());
        }
        if (condition.getPositionId() != null) {
            where.append(" and p.id = :positionId");
            params.put("positionId", condition.getPositionId());
        }
        if (condition.getStatus() != null) {
            where.append(" and u.status = :status");
            params.put("status", condition.getStatus());
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
