package com.example.bibecoding01.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoginUserModelAdvice {

    @ModelAttribute("loginUser")
    public SessionUser loginUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin(@ModelAttribute("loginUser") SessionUser loginUser) {
        return loginUser != null && loginUser.isAdmin();
    }
}
