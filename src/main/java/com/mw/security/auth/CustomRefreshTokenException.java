package com.mw.security.auth;

public class CustomRefreshTokenException extends RuntimeException{
    public CustomRefreshTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomRefreshTokenException(String msg) {
        super(msg);
    }

}
