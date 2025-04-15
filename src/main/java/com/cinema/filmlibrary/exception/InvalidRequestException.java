package com.cinema.filmlibrary.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends RuntimeException {
    private final HttpStatus status;

    public InvalidRequestException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
