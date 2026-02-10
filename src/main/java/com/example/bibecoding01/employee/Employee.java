package com.example.bibecoding01.employee;

import com.example.bibecoding01.department.Department;
import com.example.bibecoding01.position.Position;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Legacy placeholder. Employee domain is merged into User entity.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Deprecated
public class Employee {

    private Long id;
    private String employeeNo;
    private String name;
    private String email;
    private Department department;
    private Position position;
    private EmployeeStatus status;
    private LocalDate hiredAt;

    public Employee(String employeeNo, String name, String email, Department department, Position position, EmployeeStatus status, LocalDate hiredAt) {
        this.employeeNo = employeeNo;
        this.name = name;
        this.email = email;
        this.department = department;
        this.position = position;
        this.status = status;
        this.hiredAt = hiredAt;
    }

    public void update(String name, String email, Department department, Position position, EmployeeStatus status, LocalDate hiredAt) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.position = position;
        this.status = status;
        this.hiredAt = hiredAt;
    }
}
