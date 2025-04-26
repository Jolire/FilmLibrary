package com.cinema.filmlibrary.service;

import org.springframework.stereotype.Service;

@Service
public class RequestCounterService {

    private int allFilmsRequestCount = 0;

    public synchronized void incrementAllFilmsRequestCount() {
        allFilmsRequestCount++;
    }

    public synchronized int getAllFilmsRequestCount() {
        return allFilmsRequestCount;
    }

    public synchronized void resetAllFilmsRequestCount() {
        allFilmsRequestCount = 0;
    }
}
