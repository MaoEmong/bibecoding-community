package com.example.bibecoding01.department;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public List<DepartmentResponse.OptionDTO> findOptions(Long selectedId) {
        return departmentRepository.findAll().stream()
                .map(d -> new DepartmentResponse.OptionDTO(d.getId(), d.getName(), d.getId().equals(selectedId)))
                .toList();
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("부서를 찾을 수 없습니다."));
    }

    @Transactional
    public Long save(DepartmentRequest.SaveDTO request, SessionUser sessionUser) {
        requireAdmin(sessionUser);
        String name = request.getName().trim();

        departmentRepository.findByName(name)
                .ifPresent(existing -> {
                    throw new BadRequestException("이미 존재하는 부서명입니다.");
                });

        Department department = new Department(name);
        return departmentRepository.save(department).getId();
    }

    private void requireAdmin(SessionUser sessionUser) {
        if (sessionUser == null || !sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }
}
