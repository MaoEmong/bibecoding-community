package com.example.bibecoding01.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.employee.EmployeeStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

@SpringBootTest
@Transactional
class UserAdminControllerIntegrationTest {

    @Autowired
    private UserAdminController userAdminController;

    @Autowired
    private UserRepository userRepository;

    private final SessionUser adminUser = new SessionUser(1L, "홍길동", "ssar@nate.com", UserRole.ADMIN);

    @Test
    void list_populates_required_model_fields() {
        ExtendedModelMap model = new ExtendedModelMap();

        String viewName = userAdminController.list(model);

        assertThat(viewName).isEqualTo("user/admin-list");
        assertThat(model.getAttribute("employeeNo")).isEqualTo("");
        assertThat(model.getAttribute("name")).isEqualTo("");
        assertThat(model.getAttribute("email")).isEqualTo("");
        assertThat(model.getAttribute("password")).isEqualTo("");
        assertThat(model.getAttribute("hiredAt")).isEqualTo("");
        assertThat(model.getAttribute("roles")).isNotNull();
        assertThat(model.getAttribute("statuses")).isNotNull();
    }

    @Test
    void save_validation_error_redirects_with_flash_message() {
        UserRequest.SaveDTO request = new UserRequest.SaveDTO();
        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        bindingResult.addError(new FieldError("request", "name", "required"));

        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = userAdminController.save(request, bindingResult, session, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/admin/users");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("userError");
        assertThat(redirectAttributes.getFlashAttributes()).containsKey("request");
    }

    @Test
    void save_success_persists_user() {
        UserRequest.SaveDTO request = new UserRequest.SaveDTO();
        request.setEmployeeNo("EMP-9999");
        request.setName("테스트");
        request.setEmail("test9999@company.com");
        request.setPassword("1234");
        request.setRole(UserRole.EMPLOYEE);
        request.setDepartmentId(1L);
        request.setPositionId(1L);
        request.setStatus(EmployeeStatus.EMPLOYED);
        request.setHiredAt(LocalDate.of(2026, 2, 10));

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(AuthConst.LOGIN_USER, adminUser);
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String viewName = userAdminController.save(request, bindingResult, session, redirectAttributes);

        assertThat(viewName).isEqualTo("redirect:/admin/users");
        assertThat(userRepository.findByEmail("test9999@company.com")).isPresent();
    }
}
