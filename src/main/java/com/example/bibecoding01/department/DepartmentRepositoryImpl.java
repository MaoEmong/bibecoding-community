package com.example.bibecoding01.department;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class DepartmentRepositoryImpl implements DepartmentRepository {

    private final EntityManager em;

    public DepartmentRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Department save(Department department) {
        if (department.getId() == null) {
            em.persist(department);
            return department;
        }
        return em.merge(department);
    }

    @Override
    public Optional<Department> findById(Long id) {
        return Optional.ofNullable(em.find(Department.class, id));
    }

    @Override
    public Optional<Department> findByName(String name) {
        List<Department> result = em.createQuery(
                        "select d from Department d where d.name = :name",
                        Department.class
                )
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public List<Department> findAll() {
        return em.createQuery("select d from Department d order by d.id asc", Department.class)
                .getResultList();
    }
}
