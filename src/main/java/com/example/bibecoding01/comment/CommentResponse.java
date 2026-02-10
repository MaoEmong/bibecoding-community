package com.example.bibecoding01.comment;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    public record ItemDTO(
            Long id,
            Long authorId,
            String authorName,
            String authorEmail,
            String content,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean editable
    ) {
        public static ItemDTO from(Comment comment, Long loginUserId, boolean admin) {
            boolean editable = admin || comment.getAuthor().getId().equals(loginUserId);
            return new ItemDTO(
                    comment.getId(),
                    comment.getAuthor().getId(),
                    comment.getAuthor().getName(),
                    comment.getAuthor().getEmail(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getUpdatedAt(),
                    editable
            );
        }
    }

    public record EditFormDTO(
            Long postId,
            Long commentId,
            String content
    ) {
    }

    public static List<ItemDTO> listOf(List<Comment> comments, Long loginUserId, boolean admin) {
        return comments.stream()
                .map(comment -> ItemDTO.from(comment, loginUserId, admin))
                .toList();
    }
}
