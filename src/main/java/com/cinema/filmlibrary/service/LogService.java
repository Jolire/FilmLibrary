package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.exception.FileProcessingException;
import com.cinema.filmlibrary.exception.InvalidValueFormatException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
public class LogService {

    private final String logFilePath;

    public LogService() {
        this("app.log"); // значение по умолчанию
    }

    public LogService(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public ResponseEntity<Resource> downloadLogs(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate logDate = LocalDate.parse(date, formatter);

            Path path = Paths.get(logFilePath);
            if (!Files.exists(path)) {
                throw new ResourceNotFoundException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "File doesn't exist: " + logFilePath);
            }

            String formattedDate = logDate.format(formatter);
            List<String> logLines = Files.readAllLines(path);
            List<String> currentLogs = logLines.stream()
                    .filter(line -> line.startsWith(formattedDate))
                    .toList();

            if (currentLogs.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "There are no logs for specified date: " + date);
            }

            FileAttribute<Set<PosixFilePermission>> attr =
                    PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            Path logFile = Files.createTempFile("logs-" + logDate, ".log", attr);

            Files.write(logFile, currentLogs);

            Resource resource = new UrlResource(logFile.toUri());
            logFile.toFile().deleteOnExit();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (DateTimeParseException e) {
            throw new InvalidValueFormatException(HttpStatus.BAD_REQUEST,
                    "Invalid date format. Required dd-mm-yyyy");
        } catch (IOException e) {
            throw new FileProcessingException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error processing log file. " + e.getMessage());
        }
    }
}