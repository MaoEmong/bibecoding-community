package com.example.bibecoding01.position;

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
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pageTitle", "직급 관리");
        model.addAttribute("positions", positionService.findAll());
        model.addAttribute("request", new PositionRequest.SaveDTO());
        return "position/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("request") PositionRequest.SaveDTO request, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        positionService.save(request, sessionUser);
        return "redirect:/positions";
    }
}
