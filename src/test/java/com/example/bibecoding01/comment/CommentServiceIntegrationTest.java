package com.example.bibecoding01.comment;

import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    private final SessionUser adminUser = new SessionUser(1L, "Hong Gil Dong", "ssar@nate.com", UserRole.ADMIN);
    private final SessionUser writerUser = new SessionUser(2L, "Kim Min Su", "minsu@company.com", UserRole.EMPLOYEE);
    private final SessionUser otherUser = new SessionUser(3L, "Lee Seo Yeon", "seoyeon@company.com", UserRole.EMPLOYEE);

    @Test
    void author_or_admin_can_delete_but_other_cannot() {
        CommentRequest.SaveDTO save = new CommentRequest.SaveDTO();
        save.setContent("comment for delete");

        Long commentId = commentService.save(1L, save, writerUser);

        assertThrows(ForbiddenException.class, () -> commentService.delete(1L, commentId, otherUser));

        commentService.delete(1L, commentId, writerUser);

        Long commentId2 = commentService.save(1L, save, writerUser);
        commentService.delete(1L, commentId2, adminUser);
    }

    @Test
    void author_or_admin_can_update_but_other_cannot() {
        CommentRequest.SaveDTO save = new CommentRequest.SaveDTO();
        save.setContent("original comment");
        Long commentId = commentService.save(1L, save, writerUser);
        List<CommentResponse.ItemDTO> initialComments = commentService.findByPostId(1L, writerUser);
        CommentResponse.ItemDTO initial = initialComments.get(initialComments.size() - 1);
        assertTrue(initial.updatedAt() != null);
        assertEquals(initial.createdAt(), initial.updatedAt());

        CommentRequest.UpdateDTO update = new CommentRequest.UpdateDTO();
        update.setContent("updated comment");

        assertThrows(ForbiddenException.class, () -> commentService.update(1L, commentId, update, otherUser));

        commentService.update(1L, commentId, update, writerUser);
        List<CommentResponse.ItemDTO> commentsAfterWriter = commentService.findByPostId(1L, writerUser);
        CommentResponse.ItemDTO updatedByWriter = commentsAfterWriter.get(commentsAfterWriter.size() - 1);
        assertEquals("updated comment", updatedByWriter.content());
        assertTrue(updatedByWriter.updatedAt().isAfter(initial.updatedAt())
                || updatedByWriter.updatedAt().isEqual(initial.updatedAt()));

        update.setContent("admin updated");
        commentService.update(1L, commentId, update, adminUser);
        List<CommentResponse.ItemDTO> commentsAfterAdmin = commentService.findByPostId(1L, writerUser);
        CommentResponse.ItemDTO updatedByAdmin = commentsAfterAdmin.get(commentsAfterAdmin.size() - 1);
        assertEquals("admin updated", updatedByAdmin.content());
        assertTrue(updatedByAdmin.updatedAt().isAfter(updatedByWriter.updatedAt())
                || updatedByAdmin.updatedAt().isEqual(updatedByWriter.updatedAt()));
    }

    @Test
    void xss_and_html_tag_are_sanitized() {
        CommentRequest.SaveDTO save = new CommentRequest.SaveDTO();
        save.setContent("<b>bold</b><script>alert('xss')</script>");

        commentService.save(1L, save, writerUser);
        List<CommentResponse.ItemDTO> comments = commentService.findByPostId(1L, writerUser);

        CommentResponse.ItemDTO latest = comments.get(comments.size() - 1);
        assertFalse(latest.content().contains("<b>"));
        assertFalse(latest.content().contains("<script>"));
        assertTrue(latest.content().contains("bold"));
    }

    @Test
    void comments_can_be_sorted_by_updated_desc() {
        CommentRequest.SaveDTO save = new CommentRequest.SaveDTO();
        save.setContent("first");
        Long firstId = commentService.save(1L, save, writerUser);

        save.setContent("second");
        Long secondId = commentService.save(1L, save, writerUser);

        CommentRequest.UpdateDTO update = new CommentRequest.UpdateDTO();
        update.setContent("first-updated");
        commentService.update(1L, firstId, update, writerUser);

        List<CommentResponse.ItemDTO> createdAsc = commentService.findByPostId(1L, writerUser, "createdAsc");
        int firstIndexInCreatedAsc = findIndexById(createdAsc, firstId);
        int secondIndexInCreatedAsc = findIndexById(createdAsc, secondId);
        assertTrue(firstIndexInCreatedAsc >= 0);
        assertTrue(secondIndexInCreatedAsc >= 0);
        assertTrue(firstIndexInCreatedAsc < secondIndexInCreatedAsc);

        List<CommentResponse.ItemDTO> updatedDesc = commentService.findByPostId(1L, writerUser, "updatedDesc");
        int firstIndexInUpdatedDesc = findIndexById(updatedDesc, firstId);
        int secondIndexInUpdatedDesc = findIndexById(updatedDesc, secondId);
        assertTrue(firstIndexInUpdatedDesc >= 0);
        assertTrue(secondIndexInUpdatedDesc >= 0);
        assertTrue(firstIndexInUpdatedDesc < secondIndexInUpdatedDesc);
    }

    private int findIndexById(List<CommentResponse.ItemDTO> comments, Long id) {
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).id().equals(id)) {
                return i;
            }
        }
        return -1;
    }
}
