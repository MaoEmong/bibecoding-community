package com.example.bibecoding01._core.config;

import com.example.bibecoding01._core.errors.ForbiddenException;
import com.example.bibecoding01.auth.AuthConst;
import com.example.bibecoding01.auth.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object principal = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(AuthConst.LOGIN_USER);

        if (!(principal instanceof SessionUser sessionUser) || !sessionUser.isAdmin()) {
            throw new ForbiddenException("관리자 권한이 필요합니다.");
        }
        return true;
    }
}
