package com.example.bibecoding01.employee;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Legacy placeholder. Employee domain is merged into User entity.
 */
@Deprecated
public interface EmployeeRepository {

    default Employee save(Employee employee) {
        return employee;
    }

    default Optional<Employee> findById(Long id) {
        return Optional.empty();
    }

    default Optional<Employee> findByEmployeeNo(String employeeNo) {
        return Optional.empty();
    }

    default List<Employee> findPage(EmployeeRequest.ListDTO condition) {
        return Collections.emptyList();
    }

    default long count(EmployeeRequest.ListDTO condition) {
        return 0L;
    }

    default void delete(Employee employee) {
    }
}
