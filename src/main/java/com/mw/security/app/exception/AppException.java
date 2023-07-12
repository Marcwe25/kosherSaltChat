package com.mw.security.app.exception;

public class AppException extends RuntimeException{

    public AppException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public AppException(String msg) {
        super(msg);
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException() {
        super();
    }

}
