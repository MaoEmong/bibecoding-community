package com.example.bibecoding01.comment;

import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/save")
    public String save(@PathVariable Long postId,
                       @Valid @ModelAttribute("commentForm") CommentRequest.SaveDTO commentForm,
                       BindingResult bindingResult,
                       @RequestParam(defaultValue = "createdAsc") String commentSort,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError", "댓글 내용을 입력해 주세요.");
            return "redirect:/posts/{postId}?commentSort=" + commentSort;
        }
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        commentService.save(postId, commentForm, sessionUser);
        return "redirect:/posts/{postId}?commentSort=" + commentSort;
    }

    @GetMapping("/{commentId}/update-form")
    public String updateForm(@PathVariable Long postId,
                             @PathVariable Long commentId,
                             @RequestParam(defaultValue = "createdAsc") String commentSort,
                             HttpSession session,
                             Model model) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        CommentResponse.EditFormDTO editForm = commentService.findForUpdate(postId, commentId, sessionUser);

        CommentRequest.UpdateDTO form = new CommentRequest.UpdateDTO();
        form.setContent(editForm.content());

        model.addAttribute("pageTitle", "\uB313\uAE00 \uC218\uC815");
        model.addAttribute("postId", editForm.postId());
        model.addAttribute("commentId", editForm.commentId());
        model.addAttribute("commentSort", commentSort);
        model.addAttribute("form", form);
        return "comment/update-form";
    }

    @PostMapping("/{commentId}/update")
    public String update(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @Valid @ModelAttribute("form") CommentRequest.UpdateDTO form,
                         BindingResult bindingResult,
                         @RequestParam(defaultValue = "createdAsc") String commentSort,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError", "댓글 내용을 입력해 주세요.");
            return "redirect:/posts/{postId}?commentSort=" + commentSort;
        }
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        commentService.update(postId, commentId, form, sessionUser);
        return "redirect:/posts/{postId}?commentSort=" + commentSort;
    }

    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @RequestParam(defaultValue = "createdAsc") String commentSort,
                         HttpSession session) {
        SessionUser sessionUser = (SessionUser) session.getAttribute(AuthConst.LOGIN_USER);
        commentService.delete(postId, commentId, sessionUser);
        return "redirect:/posts/{postId}?commentSort=" + commentSort;
    }
}
