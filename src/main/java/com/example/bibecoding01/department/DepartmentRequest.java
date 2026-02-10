package com.example.bibecoding01.department;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class DepartmentRequest {

    @Data
    public static class SaveDTO {

        @NotBlank
        private String name;
    }
}
