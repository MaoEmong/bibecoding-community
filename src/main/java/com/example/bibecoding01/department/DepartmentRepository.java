package com.example.bibecoding01.department;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository {

    Department save(Department department);

    Optional<Department> findById(Long id);

    Optional<Department> findByName(String name);

    List<Department> findAll();
}
