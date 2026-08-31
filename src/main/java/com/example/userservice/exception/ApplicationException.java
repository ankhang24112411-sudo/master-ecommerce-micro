package com.example.userservice.exception;


import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private int code;
    private int httpStatus;

    public ApplicationException(int code, int httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }
}