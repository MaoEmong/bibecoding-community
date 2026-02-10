package com.example.bibecoding01.department;

public class DepartmentResponse {

    public record OptionDTO(
            Long id,
            String name,
            boolean selected
    ) {
    }
}
