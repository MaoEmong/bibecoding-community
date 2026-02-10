package com.example.bibecoding01.user;

import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.employee.EmployeeStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

public class UserRequest {

    @Data
    public static class SaveDTO {

        @NotBlank
        @Pattern(regexp = "^[A-Z0-9-]{3,30}$", message = "사번 형식이 올바르지 않습니다.")
        private String employeeNo;

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        @NotNull
        private UserRole role;

        @NotNull
        private Long departmentId;

        @NotNull
        private Long positionId;

        @NotNull
        private EmployeeStatus status;

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        private LocalDate hiredAt;
    }
}
