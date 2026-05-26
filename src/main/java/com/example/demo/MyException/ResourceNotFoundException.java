package com.example.demo.MyException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
