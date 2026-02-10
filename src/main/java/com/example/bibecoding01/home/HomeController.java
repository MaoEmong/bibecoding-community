package com.example.bibecoding01.home;

import com.example.bibecoding01.auth.AuthConst;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root(HttpSession session) {
        Object loginUser = session.getAttribute(AuthConst.LOGIN_USER);
        if (loginUser == null) {
            return "redirect:/login-form";
        }
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("pageTitle", "메인");
        return "home/main";
    }
}
