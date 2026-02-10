package com.example.bibecoding01._core.errors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response, Model model) {
        log.warn("business-exception uri={} message={}", request.getRequestURI(), e.getMessage());
        response.setStatus(e.getStatus().value());
        model.addAttribute("pageTitle", "\uC54C\uB9BC");
        model.addAttribute("message", e.getMessage());
        model.addAttribute("redirectUrl", resolveRedirectUrl(e, request));
        return "_core/error/alert";
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(Exception e, HttpServletRequest request, Model model) {
        log.warn("validation-exception uri={} message={}", request.getRequestURI(), e.getMessage());
        model.addAttribute("pageTitle", "\uC785\uB825 \uC624\uB958");
        model.addAttribute("message", "\uC785\uB825\uAC12\uC744 \uD655\uC778\uD574 \uC8FC\uC138\uC694.");
        model.addAttribute("redirectUrl", resolveBackUrl(request));
        return "_core/error/alert";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e, HttpServletRequest request, Model model) {
        log.error("internal-exception uri={}", request.getRequestURI(), e);
        model.addAttribute("pageTitle", "\uC11C\uBC84 \uC624\uB958");
        model.addAttribute("message", "\uC11C\uBC84 \uC624\uB958\uAC00 \uBC1C\uC0DD\uD588\uC2B5\uB2C8\uB2E4.");
        model.addAttribute("redirectUrl", resolveBackUrl(request));
        return "_core/error/alert";
    }

    private String resolveRedirectUrl(BusinessException e, HttpServletRequest request) {
        if (e instanceof UnauthenticatedException) {
            return "/login-form";
        }
        return resolveBackUrl(request);
    }

    private String resolveBackUrl(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/main";
        }
        try {
            java.net.URI uri = java.net.URI.create(referer);
            String path = uri.getPath();
            if (path == null || path.isBlank()) {
                return "/main";
            }
            String query = uri.getQuery();
            return query == null ? path : path + "?" + query;
        } catch (Exception ignored) {
            return "/main";
        }
    }
}
