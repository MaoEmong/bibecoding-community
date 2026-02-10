package com.example.bibecoding01.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PostRequest {

    @Data
    public static class SaveDTO {

        @NotBlank
        @Size(max = 120)
        private String title;

        @NotBlank
        @Size(max = 5000)
        private String content;
    }

    @Data
    public static class UpdateDTO {

        @NotBlank
        @Size(max = 120)
        private String title;

        @NotBlank
        @Size(max = 5000)
        private String content;
    }

    @Data
    public static class ListDTO {

        private String keyword;
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
