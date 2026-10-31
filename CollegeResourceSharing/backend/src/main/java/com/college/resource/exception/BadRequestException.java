package com.college.resource.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
