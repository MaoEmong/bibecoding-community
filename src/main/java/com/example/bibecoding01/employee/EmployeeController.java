package com.example.bibecoding01.employee;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.department.DepartmentService;
import com.example.bibecoding01.position.PositionService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    public EmployeeController(EmployeeService employeeService,
                              DepartmentService departmentService,
                              PositionService positionService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.positionService = positionService;
    }

    @ModelAttribute("condition")
    public EmployeeRequest.ListDTO condition() {
        return new EmployeeRequest.ListDTO();
    }

    @GetMapping
    public String list(@ModelAttribute("condition") EmployeeRequest.ListDTO condition, Model model) {
        EmployeeResponse.PageDTO page = employeeService.findPage(condition);
        model.addAttribute("pageTitle", "\uC0AC\uC6D0 \uBAA9\uB85D");
        model.addAttribute("page", page);
        model.addAttribute("employeeNo", condition.getEmployeeNo() == null ? "" : condition.getEmployeeNo());
        model.addAttribute("name", condition.getName() == null ? "" : condition.getName());
        model.addAttribute("statuses", EmployeeStatus.values());
        model.addAttribute("departmentOptions", departmentService.findOptions(condition.getDepartmentId()));
        model.addAttribute("positionOptions", positionService.findOptions(condition.getPositionId()));
        return "employee/list";
    }

    @GetMapping("/save-form")
    public String saveForm() {
        return "redirect:/admin/users";
    }

    @PostMapping("/save")
    public String save() {
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "\uC0AC\uC6D0 \uC0C1\uC138");
        model.addAttribute("employee", employeeService.findDetail(id));
        return "employee/detail";
    }

    @GetMapping("/{id}/update-form")
    public String updateForm(@PathVariable Long id, Model model) {
        EmployeeResponse.DetailDTO detail = employeeService.findDetail(id);

        EmployeeRequest.UpdateDTO form = new EmployeeRequest.UpdateDTO();
        form.setName(detail.name());
        form.setEmail(detail.email());
        form.setDepartmentId(detail.departmentId());
        form.setPositionId(detail.positionId());
        form.setStatus(detail.status());
        form.setHiredAt(detail.hiredAt());

        model.addAttribute("id", id);
        model.addAttribute("pageTitle", "\uC0AC\uC6D0 \uC218\uC815");
        model.addAttribute("form", form);
        model.addAttribute("statuses", EmployeeStatus.values());
        model.addAttribute("departmentOptions", departmentService.findOptions(detail.departmentId()));
        model.addAttribute("positionOptions", positionService.findOptions(detail.positionId()));
        return "employee/update-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") EmployeeRequest.UpdateDTO form,
                         HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        employeeService.update(id, form, sessionUser);
        return "redirect:/employees/{id}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        employeeService.delete(id, sessionUser);
        return "redirect:/employees";
    }
}
