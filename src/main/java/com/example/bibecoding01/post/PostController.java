package com.example.bibecoding01.post;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import com.example.bibecoding01.comment.CommentRequest;
import com.example.bibecoding01.comment.CommentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @ModelAttribute("condition")
    public PostRequest.ListDTO condition() {
        return new PostRequest.ListDTO();
    }

    @GetMapping
    public String list(@ModelAttribute("condition") PostRequest.ListDTO condition, Model model) {
        model.addAttribute("pageTitle", "\uAC8C\uC2DC\uD310");
        model.addAttribute("page", postService.findPage(condition));
        model.addAttribute("keyword", condition.getKeyword() == null ? "" : condition.getKeyword());
        return "post/list";
    }

    @GetMapping("/save-form")
    public String saveForm(Model model) {
        model.addAttribute("pageTitle", "\uAC8C\uC2DC\uAE00 \uC791\uC131");
        model.addAttribute("form", new PostRequest.SaveDTO());
        return "post/save-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("form") PostRequest.SaveDTO form, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        postService.save(form, sessionUser);
        return "redirect:/posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "createdAsc") String commentSort,
                         HttpSession session,
                         Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        model.addAttribute("pageTitle", "\uAC8C\uC2DC\uAE00 \uC0C1\uC138");
        model.addAttribute("post", postService.findDetail(id, sessionUser));
        model.addAttribute("comments", commentService.findByPostId(id, sessionUser, commentSort));
        model.addAttribute("commentSort", commentSort);
        model.addAttribute("isCreatedAscSort", "createdAsc".equals(commentSort));
        model.addAttribute("isUpdatedDescSort", "updatedDesc".equals(commentSort));
        model.addAttribute("commentForm", new CommentRequest.SaveDTO());
        return "post/detail";
    }

    @GetMapping("/{id}/update-form")
    public String updateForm(@PathVariable Long id, HttpSession session, Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        PostResponse.DetailDTO detail = postService.findDetail(id, sessionUser);

        PostRequest.UpdateDTO form = new PostRequest.UpdateDTO();
        form.setTitle(detail.title());
        form.setContent(detail.content());

        model.addAttribute("pageTitle", "\uAC8C\uC2DC\uAE00 \uC218\uC815");
        model.addAttribute("id", id);
        model.addAttribute("form", form);
        model.addAttribute("editable", detail.editable());
        return "post/update-form";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") PostRequest.UpdateDTO form,
                         HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        postService.update(id, form, sessionUser);
        return "redirect:/posts/{id}";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        postService.delete(id, sessionUser);
        return "redirect:/posts";
    }
}
