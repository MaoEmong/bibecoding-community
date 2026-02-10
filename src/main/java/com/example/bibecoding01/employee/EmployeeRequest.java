package com.example.bibecoding01.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

public class EmployeeRequest {

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

    @Data
    public static class UpdateDTO {

        @NotBlank
        private String name;

        @NotBlank
        @Email
        private String email;

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

    @Data
    public static class ListDTO {

        private String employeeNo;
        private String name;
        private Long departmentId;
        private Long positionId;
        private EmployeeStatus status;
        private int page = 0;
        private int size = 10;

        public void setPage(int page) {
            this.page = Math.max(page, 0);
        }

        public void setSize(int size) {
            this.size = size <= 0 ? 10 : Math.min(size, 50);
        }
    }
}
