package com.example.bibecoding01.department;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "부서 관리");
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("request", new DepartmentRequest.SaveDTO());
        return "department/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("request") DepartmentRequest.SaveDTO request, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        departmentService.save(request, sessionUser);
        return "redirect:/departments";
    }
}
