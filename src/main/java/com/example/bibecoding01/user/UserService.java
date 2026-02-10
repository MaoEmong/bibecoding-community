package com.example.bibecoding01.user;

import com.example.bibecoding01._core.errors.BadRequestException;
import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.UnauthenticatedException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.department.Department;
import com.example.bibecoding01.department.DepartmentService;
import com.example.bibecoding01.position.Position;
import com.example.bibecoding01.position.PositionService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public UserService(UserRepository userRepository,
                       DepartmentService departmentService,
                       PositionService positionService) {
        this.userRepository = userRepository;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthenticatedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!user.getPassword().equals(password)) {
            throw new UnauthenticatedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        return user;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public Long save(UserRequest.SaveDTO request, SessionUser sessionUser) {
        requireAdmin(sessionUser);

        userRepository.findByEmail(request.getEmail())
                .ifPresent(existing -> {
                    throw new BadRequestException("이미 존재하는 이메일입니다.");
                });

        userRepository.findByEmployeeNo(request.getEmployeeNo())
                .ifPresent(existing -> {
                    throw new BadRequestException("이미 사용 중인 사번입니다.");
                });

        Department department = departmentService.findById(request.getDepartmentId());
        Position position = positionService.findById(request.getPositionId());

        User user = new User(
                request.getEmployeeNo(),
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                department,
                position,
                request.getStatus(),
                request.getHiredAt()
        );
        return userRepository.save(user).getId();
    }

    private void requireAdmin(SessionUser sessionUser) {
        if (sessionUser == null || !sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
    }
}
