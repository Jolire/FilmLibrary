package com.cinema.filmlibrary.service;

import org.springframework.stereotype.Service;

/** Main method. */
@Service
public class RequestCounterService {

    private int requestCount = 0;

    /** Main method. */
    public synchronized void incrementAllFilmsRequestCount() {
        requestCount++;
    }

    public synchronized int getRequestCount() {
        return requestCount;
    }

    /** Main method. */
    public synchronized void resetAllFilmsRequestCount() {
        requestCount = 0;
    }
}
