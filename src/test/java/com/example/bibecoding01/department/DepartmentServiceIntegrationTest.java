package com.example.bibecoding01.department;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DepartmentServiceIntegrationTest {

    @Autowired
    private DepartmentService departmentService;

    private final SessionUser adminUser = new SessionUser(1L, "홍길동", "ssar@nate.com", UserRole.ADMIN);

    @Test
    void add_department_success() {
        DepartmentRequest.SaveDTO request = new DepartmentRequest.SaveDTO();
        request.setName("법무");

        departmentService.save(request, adminUser);

        assertFalse(departmentService.findAll().isEmpty());
    }

    @Test
    void duplicate_department_throws_exception() {
        DepartmentRequest.SaveDTO request = new DepartmentRequest.SaveDTO();
        request.setName("인사");

        assertThrows(BadRequestException.class, () -> departmentService.save(request, adminUser));
    }
}
