package com.example.bibecoding01.employee;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.department.DepartmentService;
import com.example.bibecoding01.position.PositionService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PositionService positionService;

    private final SessionUser adminUser = new SessionUser(1L, "홍길동", "ssar@nate.com", UserRole.ADMIN);
    private final SessionUser employeeUser = new SessionUser(2L, "김민수", "minsu@company.com", UserRole.EMPLOYEE);

    @Test
    void save_then_find_detail_success() {
        Long departmentId = departmentService.findAll().get(0).getId();
        Long positionId = positionService.findAll().get(0).getId();

        EmployeeRequest.SaveDTO request = new EmployeeRequest.SaveDTO();
        request.setEmployeeNo("EMP-0999");
        request.setName("홍길동");
        request.setEmail("hong@example.com");
        request.setDepartmentId(departmentId);
        request.setPositionId(positionId);
        request.setStatus(EmployeeStatus.EMPLOYED);
        request.setHiredAt(LocalDate.of(2025, 2, 1));

        Long id = employeeService.save(request, adminUser);
        EmployeeResponse.DetailDTO detail = employeeService.findDetail(id);

        assertEquals("EMP-0999", detail.employeeNo());
        assertEquals("홍길동", detail.name());
        assertNotNull(detail.positionId());
    }

    @Test
    void employee_role_cannot_save() {
        Long departmentId = departmentService.findAll().get(0).getId();
        Long positionId = positionService.findAll().get(0).getId();

        EmployeeRequest.SaveDTO request = new EmployeeRequest.SaveDTO();
        request.setEmployeeNo("EMP-0899");
        request.setName("권한테스트");
        request.setEmail("role@test.com");
        request.setDepartmentId(departmentId);
        request.setPositionId(positionId);
        request.setStatus(EmployeeStatus.EMPLOYED);
        request.setHiredAt(LocalDate.of(2025, 2, 1));

        assertThrows(ForbiddenException.class, () -> employeeService.save(request, employeeUser));
    }

    @Test
    void duplicate_employee_no_throws_bad_request() {
        Long departmentId = departmentService.findAll().get(0).getId();
        Long positionId = positionService.findAll().get(0).getId();

        EmployeeRequest.SaveDTO request = new EmployeeRequest.SaveDTO();
        request.setEmployeeNo("EMP-0001");
        request.setName("중복사번");
        request.setEmail("dup@example.com");
        request.setDepartmentId(departmentId);
        request.setPositionId(positionId);
        request.setStatus(EmployeeStatus.EMPLOYED);
        request.setHiredAt(LocalDate.of(2024, 1, 10));

        assertThrows(BadRequestException.class, () -> employeeService.save(request, adminUser));
    }

    @Test
    void update_and_delete_success() {
        Long departmentId1 = departmentService.findAll().get(0).getId();
        Long departmentId2 = departmentService.findAll().get(1).getId();
        Long positionId1 = positionService.findAll().get(0).getId();
        Long positionId2 = positionService.findAll().get(1).getId();

        EmployeeRequest.SaveDTO saveRequest = new EmployeeRequest.SaveDTO();
        saveRequest.setEmployeeNo("EMP-0888");
        saveRequest.setName("수정전");
        saveRequest.setEmail("before@example.com");
        saveRequest.setDepartmentId(departmentId1);
        saveRequest.setPositionId(positionId1);
        saveRequest.setStatus(EmployeeStatus.EMPLOYED);
        saveRequest.setHiredAt(LocalDate.of(2023, 8, 20));

        Long id = employeeService.save(saveRequest, adminUser);

        EmployeeRequest.UpdateDTO updateRequest = new EmployeeRequest.UpdateDTO();
        updateRequest.setName("수정후");
        updateRequest.setEmail("after@example.com");
        updateRequest.setDepartmentId(departmentId2);
        updateRequest.setPositionId(positionId2);
        updateRequest.setStatus(EmployeeStatus.LEAVE);
        updateRequest.setHiredAt(LocalDate.of(2023, 8, 20));

        employeeService.update(id, updateRequest, adminUser);

        EmployeeResponse.DetailDTO updated = employeeService.findDetail(id);
        assertEquals("수정후", updated.name());
        assertEquals(positionId2, updated.positionId());

        employeeService.delete(id, adminUser);
        assertThrows(NotFoundException.class, () -> employeeService.findDetail(id));
    }
}
