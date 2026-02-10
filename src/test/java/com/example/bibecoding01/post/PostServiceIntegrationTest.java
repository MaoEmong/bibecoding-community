package com.example.bibecoding01.post;

import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.auth.UserRole;
import com.example.bibecoding01.comment.CommentRepository;
import com.example.bibecoding01.comment.CommentRequest;
import com.example.bibecoding01.comment.CommentService;
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
class PostServiceIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    private final SessionUser adminUser = new SessionUser(1L, "Hong Gil Dong", "ssar@nate.com", UserRole.ADMIN);
    private final SessionUser writerUser = new SessionUser(2L, "Kim Min Su", "minsu@company.com", UserRole.EMPLOYEE);
    private final SessionUser otherUser = new SessionUser(3L, "Lee Seo Yeon", "seoyeon@company.com", UserRole.EMPLOYEE);

    @Test
    void author_or_admin_can_update_but_other_cannot() {
        PostRequest.SaveDTO save = new PostRequest.SaveDTO();
        save.setTitle("test title");
        save.setContent("content");

        Long id = postService.save(save, writerUser);

        PostRequest.UpdateDTO update = new PostRequest.UpdateDTO();
        update.setTitle("updated");
        update.setContent("updated content");

        assertThrows(ForbiddenException.class, () -> postService.update(id, update, otherUser));

        postService.update(id, update, writerUser);
        postService.update(id, update, adminUser);
    }

    @Test
    void xss_script_tag_is_sanitized() {
        PostRequest.SaveDTO save = new PostRequest.SaveDTO();
        save.setTitle("<b>title</b>");
        save.setContent("<p>safe</p><script>alert('xss')</script><b>bold</b>");

        Long id = postService.save(save, writerUser);
        PostResponse.DetailDTO detail = postService.findDetail(id, writerUser);

        assertFalse(detail.content().contains("<script>"));
        assertFalse(detail.content().contains("alert('xss')"));
        assertTrue(detail.content().contains("<b>bold</b>"));
    }

    @Test
    void deleting_post_also_deletes_its_comments() {
        PostRequest.SaveDTO savePost = new PostRequest.SaveDTO();
        savePost.setTitle("post with comments");
        savePost.setContent("body");
        Long postId = postService.save(savePost, writerUser);

        CommentRequest.SaveDTO saveComment = new CommentRequest.SaveDTO();
        saveComment.setContent("first comment");
        commentService.save(postId, saveComment, writerUser);

        assertEquals(1, commentRepository.findByPostId(postId).size());

        postService.delete(postId, writerUser);

        assertEquals(0, commentRepository.findByPostId(postId).size());
    }
}
