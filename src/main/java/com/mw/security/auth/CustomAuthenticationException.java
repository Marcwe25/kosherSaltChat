package com.mw.security.auth;

import org.springframework.security.core.AuthenticationException;

public class CustomAuthenticationException extends AuthenticationException {

    String securityMessage;

    public CustomAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
        this.securityMessage=msg;
    }

    public CustomAuthenticationException(Throwable cause) {
        super("see throwable", cause);
        this.securityMessage="see throwable";
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
        this.securityMessage=msg;
    }
}
