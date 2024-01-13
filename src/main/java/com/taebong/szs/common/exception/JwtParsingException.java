package com.taebong.szs.common.exception;

public class JwtParsingException extends RuntimeException {
    public JwtParsingException(String message) {
        super(message);
    }

    public JwtParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}