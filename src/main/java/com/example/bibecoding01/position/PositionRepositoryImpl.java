package com.example.bibecoding01.position;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PositionRepositoryImpl implements PositionRepository {

    private final EntityManager em;

    public PositionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Position save(Position position) {
        if (position.getId() == null) {
            em.persist(position);
            return position;
        }
        return em.merge(position);
    }

    @Override
    public Optional<Position> findById(Long id) {
        return Optional.ofNullable(em.find(Position.class, id));
    }

    @Override
    public Optional<Position> findByName(String name) {
        List<Position> result = em.createQuery(
                        "select p from Position p where p.name = :name",
                        Position.class
                )
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return result.stream().findFirst();
    }

    @Override
    public List<Position> findAll() {
        return em.createQuery("select p from Position p order by p.id asc", Position.class)
                .getResultList();
    }
}
