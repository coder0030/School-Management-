package com.example.demo.MyException;

public class IncompletInformationException extends RuntimeException{
    public IncompletInformationException(String msg) {
        super(msg);
    }
}
