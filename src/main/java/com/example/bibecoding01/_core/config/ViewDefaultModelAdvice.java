package com.example.bibecoding01._core.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewDefaultModelAdvice {

    @ModelAttribute("keyword")
    public String keyword() {
        return "";
    }

    @ModelAttribute("employeeNo")
    public String employeeNo() {
        return "";
    }

    @ModelAttribute("name")
    public String name() {
        return "";
    }
}
