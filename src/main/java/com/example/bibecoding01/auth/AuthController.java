package com.example.bibecoding01.auth;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login-form")
    public String loginForm(Model model) {
        model.addAttribute("pageTitle", "로그인");
        return "auth/login-form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginRequest request, HttpSession session) {
        SessionUser sessionUser = authService.login(request.getEmail(), request.getPassword());
        session.setAttribute(AuthConst.LOGIN_USER, sessionUser);
        return "redirect:/main";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login-form";
    }
}
