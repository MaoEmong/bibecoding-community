package com.example.bibecoding01.comment;

import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.post.Post;
import com.example.bibecoding01.post.PostRepository;
import com.example.bibecoding01.user.User;
import com.example.bibecoding01.user.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public List<CommentResponse.ItemDTO> findByPostId(Long postId, SessionUser sessionUser) {
        return findByPostId(postId, sessionUser, "createdAsc");
    }

    public List<CommentResponse.ItemDTO> findByPostId(Long postId, SessionUser sessionUser, String sort) {
        requirePost(postId);
        List<Comment> comments = commentRepository.findByPostId(postId);
        comments.sort(resolveSortComparator(sort));
        return CommentResponse.listOf(comments, sessionUser.id(), sessionUser.isAdmin());
    }

    public CommentResponse.EditFormDTO findForUpdate(Long postId, Long commentId, SessionUser sessionUser) {
        requirePost(postId);
        Comment comment = requireComment(commentId);
        requireSamePost(postId, comment);
        requireEditable(comment, sessionUser);
        return new CommentResponse.EditFormDTO(postId, commentId, comment.getContent());
    }

    @Transactional
    public Long save(Long postId, CommentRequest.SaveDTO request, SessionUser sessionUser) {
        Post post = requirePost(postId);
        User author = userRepository.findByEmail(sessionUser.email())
                .orElseThrow(() -> new NotFoundException("\uC0AC\uC6A9\uC790\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));

        String cleanContent = sanitizeContent(request.getContent());

        Comment comment = new Comment(post, author, cleanContent);
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        comment.setUpdatedAt(now);
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void update(Long postId, Long commentId, CommentRequest.UpdateDTO request, SessionUser sessionUser) {
        requirePost(postId);
        Comment comment = requireComment(commentId);
        requireSamePost(postId, comment);
        requireEditable(comment, sessionUser);
        comment.setContent(sanitizeContent(request.getContent()));
        comment.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void delete(Long postId, Long commentId, SessionUser sessionUser) {
        requirePost(postId);
        Comment comment = requireComment(commentId);
        requireSamePost(postId, comment);
        requireEditable(comment, sessionUser);
        commentRepository.delete(comment);
    }

    private Post requirePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("\uAC8C\uC2DC\uAE00\uC744 \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));
    }

    private Comment requireComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("\uB313\uAE00\uC744 \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));
    }

    private void requireSamePost(Long postId, Comment comment) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new NotFoundException("\uAC8C\uC2DC\uAE00\uC5D0 \uD574\uB2F9 \uB313\uAE00\uC774 \uC5C6\uC2B5\uB2C8\uB2E4.");
        }
    }

    private void requireEditable(Comment comment, SessionUser sessionUser) {
        if (sessionUser.isAdmin()) {
            return;
        }
        if (!comment.getAuthor().getId().equals(sessionUser.id())) {
            throw new ForbiddenException("\uB313\uAE00 \uC791\uC131\uC790 \uBCF8\uC778 \uB610\uB294 \uAD00\uB9AC\uC790\uB9CC \uC218\uC815/\uC0AD\uC81C\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4.");
        }
    }

    private String sanitizeContent(String content) {
        return Jsoup.clean(content, Safelist.none()).trim();
    }

    private Comparator<Comment> resolveSortComparator(String sort) {
        if ("updatedDesc".equals(sort)) {
            return Comparator.comparing(Comment::getUpdatedAt).reversed()
                    .thenComparing(Comment::getId, Comparator.reverseOrder());
        }
        return Comparator.comparing(Comment::getCreatedAt)
                .thenComparing(Comment::getId);
    }
}
