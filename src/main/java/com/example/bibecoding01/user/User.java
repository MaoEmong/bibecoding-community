package com.example.bibecoding01.user;

import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.department.Department;
import com.example.bibecoding01.employee.EmployeeStatus;
import com.example.bibecoding01.position.Position;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_employee_no", columnNames = "employee_no")
        }
)
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_no", nullable = false, length = 30)
    private String employeeNo;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status;

    @Column(nullable = false)
    private LocalDate hiredAt;

    public User(String employeeNo,
                String name,
                String email,
                String password,
                UserRole role,
                Department department,
                Position position,
                EmployeeStatus status,
                LocalDate hiredAt) {
        this.employeeNo = employeeNo;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
        this.position = position;
        this.status = status;
        this.hiredAt = hiredAt;
    }

    public void updateProfile(String name,
                              String email,
                              Department department,
                              Position position,
                              EmployeeStatus status,
                              LocalDate hiredAt) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.position = position;
        this.status = status;
        this.hiredAt = hiredAt;
    }
}
