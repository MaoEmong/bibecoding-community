package com.example.bibecoding01._core.errors;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends BusinessException {

    public UnauthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
