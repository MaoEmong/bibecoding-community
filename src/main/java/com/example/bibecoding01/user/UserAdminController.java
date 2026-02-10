package com.example.bibecoding01.user;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.department.DepartmentService;
import com.example.bibecoding01.employee.EmployeeStatus;
import com.example.bibecoding01.position.PositionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserService userService;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public UserAdminController(UserService userService,
                               DepartmentService departmentService,
                               PositionService positionService) {
        this.userService = userService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @GetMapping
    public String list(Model model) {
        UserRequest.SaveDTO form = model.containsAttribute("request")
                ? (UserRequest.SaveDTO) model.getAttribute("request")
                : new UserRequest.SaveDTO();

        model.addAttribute("pageTitle", "사용자 관리");
        model.addAttribute("users", userService.findAll());
        model.addAttribute("roles", roleOptions(form.getRole()));
        model.addAttribute("statuses", statusOptions(form.getStatus()));
        model.addAttribute("departmentOptions", departmentService.findOptions(form.getDepartmentId()));
        model.addAttribute("positionOptions", positionService.findOptions(form.getPositionId()));

        model.addAttribute("employeeNo", valueOf(form.getEmployeeNo()));
        model.addAttribute("name", valueOf(form.getName()));
        model.addAttribute("email", valueOf(form.getEmail()));
        model.addAttribute("password", valueOf(form.getPassword()));
        model.addAttribute("hiredAt", form.getHiredAt() == null ? "" : form.getHiredAt().toString());

        return "user/admin-list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("request") UserRequest.SaveDTO request,
                       BindingResult bindingResult,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("userError", "입력값을 확인해 주세요.");
            redirectAttributes.addFlashAttribute("request", request);
            return "redirect:/admin/users";
        }

        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        userService.save(request, sessionUser);
        return "redirect:/admin/users";
    }

    private List<RoleOption> roleOptions(UserRole selectedRole) {
        return Arrays.stream(UserRole.values())
                .map(role -> new RoleOption(role.name(), role.getLabel(), role == selectedRole))
                .toList();
    }

    private List<StatusOption> statusOptions(EmployeeStatus selectedStatus) {
        return Arrays.stream(EmployeeStatus.values())
                .map(status -> new StatusOption(status.name(), status.getLabel(), status == selectedStatus))
                .toList();
    }

    private String valueOf(String value) {
        return value == null ? "" : value;
    }

    public record RoleOption(String name, String label, boolean selected) {
    }

    public record StatusOption(String name, String label, boolean selected) {
    }
}
