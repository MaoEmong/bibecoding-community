package com.example.bibecoding01.auth;

import com.example.bibecoding01._core.errors.UnauthenticatedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void admin_login_success() {
        SessionUser user = authService.login("ssar@nate.com", "1234");

        assertEquals("홍길동", user.name());
        assertEquals(UserRole.ADMIN, user.role());
    }

    @Test
    void invalid_login_throws_exception() {
        assertThrows(UnauthenticatedException.class, () -> authService.login("ssar@nate.com", "wrong"));
    }
}
