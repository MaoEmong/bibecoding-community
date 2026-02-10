package com.example.bibecoding01.auth;

import com.example.bibecoding01.user.User;
import com.example.bibecoding01.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public SessionUser login(String email, String password) {
        User user = userService.authenticate(email, password);
        return new SessionUser(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
