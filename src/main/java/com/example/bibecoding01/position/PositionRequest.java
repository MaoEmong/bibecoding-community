package com.example.bibecoding01.position;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class PositionRequest {

    @Data
    public static class SaveDTO {

        @NotBlank
        private String name;
    }
}
