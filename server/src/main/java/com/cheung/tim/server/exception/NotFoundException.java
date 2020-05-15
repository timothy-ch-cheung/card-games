package com.cheung.tim.server.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException (String message) {
        super(message);
    }
}
