package com.example.bibecoding01.position;

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
class PositionServiceIntegrationTest {

    @Autowired
    private PositionService positionService;

    private final SessionUser adminUser = new SessionUser(1L, "홍길동", "ssar@nate.com", UserRole.ADMIN);

    @Test
    void add_position_success() {
        PositionRequest.SaveDTO request = new PositionRequest.SaveDTO();
        request.setName("이사");

        positionService.save(request, adminUser);

        assertFalse(positionService.findAll().isEmpty());
    }

    @Test
    void duplicate_position_throws_exception() {
        PositionRequest.SaveDTO request = new PositionRequest.SaveDTO();
        request.setName("사원");

        assertThrows(BadRequestException.class, () -> positionService.save(request, adminUser));
    }
}
