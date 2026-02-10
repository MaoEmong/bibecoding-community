package com.example.bibecoding01._core.config;

import com.example.bibecoding01._core.errors.UnauthenticatedException;
import com.example.bibecoding01.auth.AuthConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object loginUser = request.getSession(false) == null
                ? null
                : request.getSession(false).getAttribute(AuthConst.LOGIN_USER);

        if (loginUser == null) {
            throw new UnauthenticatedException("\uB85C\uADF8\uC778\uC774 \uD544\uC694\uD569\uB2C8\uB2E4.");
        }
        return true;
    }
}
