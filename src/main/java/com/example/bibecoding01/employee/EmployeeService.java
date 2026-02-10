package com.example.bibecoding01.employee;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.department.Department;
import com.example.bibecoding01.department.DepartmentService;
import com.example.bibecoding01.position.Position;
import com.example.bibecoding01.position.PositionService;
import com.example.bibecoding01.user.User;
import com.example.bibecoding01.user.UserQueryCondition;
import com.example.bibecoding01.user.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public EmployeeService(UserRepository userRepository,
                           DepartmentService departmentService,
                           PositionService positionService) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @Transactional
    public Long save(EmployeeRequest.SaveDTO request, SessionUser sessionUser) {
        requireAdmin(sessionUser);

        userRepository.findByEmployeeNo(request.getEmployeeNo())
                .ifPresent(user -> {
                    throw new BadRequestException("이미 사용 중인 사번입니다.");
                });

        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new BadRequestException("이미 존재하는 이메일입니다.");
                });

        Department department = departmentService.findById(request.getDepartmentId());
        Position position = positionService.findById(request.getPositionId());

        User user = new User(
                request.getEmployeeNo(),
                request.getName(),
                request.getEmail(),
                "1234",
                UserRole.EMPLOYEE,
                department,
                position,
                request.getStatus(),
                request.getHiredAt()
        );

        return userRepository.save(user).getId();
    }

    public EmployeeResponse.PageDTO findPage(EmployeeRequest.ListDTO request) {
        UserQueryCondition condition = new UserQueryCondition();
        condition.setEmployeeNo(request.getEmployeeNo());
        condition.setName(request.getName());
        condition.setDepartmentId(request.getDepartmentId());
        condition.setPositionId(request.getPositionId());
        condition.setStatus(request.getStatus());
        condition.setPage(request.getPage());
        condition.setSize(request.getSize());

        List<User> users = userRepository.findPage(condition);
        long totalCount = userRepository.count(condition);
        return EmployeeResponse.pageOf(users, request.getPage(), request.getSize(), totalCount);
    }

    public EmployeeResponse.DetailDTO findDetail(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사원을 찾을 수 없습니다."));

        return EmployeeResponse.DetailDTO.from(user);
    }

    @Transactional
    public void update(Long id, EmployeeRequest.UpdateDTO request, SessionUser sessionUser) {
        requireAdmin(sessionUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사원을 찾을 수 없습니다."));

        userRepository.findByEmail(request.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BadRequestException("이미 존재하는 이메일입니다.");
                });

        Department department = departmentService.findById(request.getDepartmentId());
        Position position = positionService.findById(request.getPositionId());

        user.updateProfile(
                request.getName(),
                request.getEmail(),
                department,
                position,
                request.getStatus(),
                request.getHiredAt()
        );
    }

    @Transactional
    public void delete(Long id, SessionUser sessionUser) {
        requireAdmin(sessionUser);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("사원을 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    private void requireAdmin(SessionUser sessionUser) {
        if (sessionUser == null || !sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }
}
