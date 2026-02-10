package com.example.bibecoding01._core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",
                        "/login-form",
                        "/login",
                        "/h2-console/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/error"
                );

        registry.addInterceptor(new AdminCheckInterceptor())
                .addPathPatterns(
                        "/employees/save-form",
                        "/employees/save",
                        "/employees/*/update-form",
                        "/employees/*/update",
                        "/employees/*/delete",
                        "/departments/**",
                        "/positions/**",
                        "/admin/**"
                );
    }
}
