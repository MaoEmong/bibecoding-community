package com.example.bibecoding01.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostResponse {

    public record DetailDTO(
            Long id,
            String title,
            String content,
            Long authorId,
            String authorName,
            String authorEmail,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean editable
    ) {
        public static DetailDTO from(Post post, Long loginUserId, boolean admin) {
            boolean editable = admin || post.getAuthor().getId().equals(loginUserId);
            return new DetailDTO(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getAuthor().getId(),
                    post.getAuthor().getName(),
                    post.getAuthor().getEmail(),
                    post.getCreatedAt(),
                    post.getUpdatedAt(),
                    editable
            );
        }
    }

    public record ListItemDTO(
            Long id,
            String title,
            String authorName,
            LocalDateTime createdAt
    ) {
        public static ListItemDTO from(Post post) {
            return new ListItemDTO(post.getId(), post.getTitle(), post.getAuthor().getName(), post.getCreatedAt());
        }
    }

    public record PageDTO(
            List<ListItemDTO> items,
            int page,
            int size,
            int totalPages,
            long totalCount,
            boolean hasPrev,
            boolean hasNext,
            Integer prevPage,
            Integer nextPage,
            List<Integer> pageNumbers
    ) {
    }

    public static PageDTO pageOf(List<Post> posts, int page, int size, long totalCount) {
        int totalPages = (int) Math.ceil(totalCount / (double) size);
        List<ListItemDTO> items = posts.stream().map(ListItemDTO::from).toList();

        boolean hasPrev = page > 0;
        boolean hasNext = page + 1 < totalPages;

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < totalPages; i++) {
            pageNumbers.add(i);
        }

        return new PageDTO(
                items,
                page,
                size,
                totalPages,
                totalCount,
                hasPrev,
                hasNext,
                hasPrev ? page - 1 : null,
                hasNext ? page + 1 : null,
                pageNumbers
        );
    }
}
