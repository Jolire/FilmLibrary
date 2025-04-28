package com.cinema.filmlibrary.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for returning specific HTTP status codes with messages.
 * Similar to Spring's ResponseStatusException but extends our BasicException.
 */
public class ResponseStatusException extends BasicException {

    /**
     * Constructor with HTTP status and message.
     *
     * @param status HTTP status code
     * @param message Error message
     */
    public ResponseStatusException(HttpStatus status, String message) {
        super(status, message);
    }

    /**
     * Constructor with HTTP status only (uses default reason phrase).
     *
     * @param status HTTP status code
     */
    public ResponseStatusException(HttpStatus status) {
        super(status, status.getReasonPhrase());
    }

    /**
     * Convenience constructor for common status codes.
     *
     * @param message Error message
     */
    public ResponseStatusException(String message) {
        this(HttpStatus.BAD_REQUEST, message);
    }
}