package com.example.demo.MyException;

public class DataSaveException extends RuntimeException{
    public DataSaveException(String msg) {
        super(msg);
    }
}
