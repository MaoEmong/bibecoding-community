package com.example.bibecoding01.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class CommentRequest {

    @Data
    public static class SaveDTO {

        @NotBlank
        @Size(max = 1000)
        private String content;
    }

    @Data
    public static class UpdateDTO {

        @NotBlank
        @Size(max = 1000)
        private String content;
    }
}
