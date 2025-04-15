package com.cinema.filmlibrary.exception;

import org.springframework.http.HttpStatus;

/** Class tp handle invalid request format exceptions. */
public class InvalidRequestFormatException extends BasicException {

    /** Constructor of the class. */
    public InvalidRequestFormatException(HttpStatus status, String message) {
        super(status, message);
    }
}
