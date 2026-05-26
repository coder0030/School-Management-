package com.example.demo.MyException;

public class IncompleteDataException extends RuntimeException{
    public IncompleteDataException(String msg) {
        super(msg);
    }
}
