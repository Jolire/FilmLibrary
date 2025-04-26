package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.exception.InvalidValueFormatException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void downloadLogs_ShouldReturnLogsForValidDate() throws Exception {
        Path logFile = tempDir.resolve("app.log");
        Files.write(logFile, List.of(
                "01-01-2023 Some log message",
                "01-01-2023 Another log message",
                "02-01-2023 Different date log"
        ));

        LogService logService = new LogService(logFile.toString());
        var response = logService.downloadLogs("01-01-2023");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void downloadLogs_ShouldThrowWhenFileNotFound() {
        LogService logService = new LogService("nonexistent.log");

        assertThrows(ResourceNotFoundException.class,
                () -> logService.downloadLogs("01-01-2023"));
    }

    @Test
    void downloadLogs_ShouldThrowWhenInvalidDateFormat() {
        LogService logService = new LogService("app.log");

        assertThrows(InvalidValueFormatException.class,
                () -> logService.downloadLogs("2023-01-01"));
    }

    @Test
    void downloadLogs_ShouldThrowWhenNoLogsForDate() throws Exception {
        Path logFile = tempDir.resolve("app.log");
        Files.write(logFile, List.of("02-01-2023 Log message"));

        LogService logService = new LogService(logFile.toString());

        assertThrows(ResponseStatusException.class,
                () -> logService.downloadLogs("01-01-2023"));
    }
}