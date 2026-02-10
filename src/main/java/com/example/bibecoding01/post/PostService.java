package com.example.bibecoding01.post;

import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01._core.errors.NotFoundException;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.comment.CommentRepository;
import com.example.bibecoding01.user.User;
import com.example.bibecoding01.user.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private static final Safelist CONTENT_SAFELIST = Safelist.relaxed()
            .addAttributes(":all", "style", "class");

    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Long save(PostRequest.SaveDTO request, SessionUser sessionUser) {
        User author = userRepository.findByEmail(sessionUser.email())
                .orElseThrow(() -> new NotFoundException("\uC0AC\uC6A9\uC790\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));

        String cleanTitle = sanitizeTitle(request.getTitle());
        String cleanContent = sanitizeContent(request.getContent());

        Post post = new Post(author, cleanTitle, cleanContent);
        LocalDateTime now = LocalDateTime.now();
        post.setCreatedAt(now);
        post.setUpdatedAt(now);
        return postRepository.save(post).getId();
    }

    public PostResponse.PageDTO findPage(PostRequest.ListDTO request) {
        List<Post> posts = postRepository.findPage(request);
        long totalCount = postRepository.count(request);
        return PostResponse.pageOf(posts, request.getPage(), request.getSize(), totalCount);
    }

    public PostResponse.DetailDTO findDetail(Long id, SessionUser sessionUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("\uAC8C\uC2DC\uAE00\uC744 \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));

        return PostResponse.DetailDTO.from(post, sessionUser.id(), sessionUser.isAdmin());
    }

    @Transactional
    public void update(Long id, PostRequest.UpdateDTO request, SessionUser sessionUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("\uAC8C\uC2DC\uAE00\uC744 \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));

        requireEditable(post, sessionUser);

        String cleanTitle = sanitizeTitle(request.getTitle());
        String cleanContent = sanitizeContent(request.getContent());
        post.update(cleanTitle, cleanContent);
        post.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void delete(Long id, SessionUser sessionUser) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("\uAC8C\uC2DC\uAE00\uC744 \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4."));

        requireEditable(post, sessionUser);
        commentRepository.deleteByPostId(id);
        postRepository.delete(post);
    }

    private void requireEditable(Post post, SessionUser sessionUser) {
        if (sessionUser.isAdmin()) {
            return;
        }
        if (!post.getAuthor().getId().equals(sessionUser.id())) {
            throw new ForbiddenException("\uC791\uC131\uC790 \uBCF8\uC778 \uB610\uB294 \uAD00\uB9AC\uC790\uB9CC \uC218\uC815/\uC0AD\uC81C\uD560 \uC218 \uC788\uC2B5\uB2C8\uB2E4.");
        }
    }

    private String sanitizeTitle(String title) {
        return Jsoup.clean(title, Safelist.none()).trim();
    }

    private String sanitizeContent(String content) {
        return Jsoup.clean(content, CONTENT_SAFELIST);
    }
}
