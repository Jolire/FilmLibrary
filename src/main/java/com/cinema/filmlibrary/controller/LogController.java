package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Class to handle logs requests. */
@RestController
@RequestMapping("/logs")
@Tag(name = "BookShop API", description = "Operations with .log file")
public class LogController {

    private static final String LOG_FILE_PATH = "app.log";

    /** Function to return .log file with logs for specified date. */
    @Operation(summary = "Get .log file", description = "Returns .log file with logs from specified date")
    @GetMapping("/get-logs")
    public ResponseEntity<Resource> downloadLogs(@RequestParam String date) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate logDate = LocalDate.parse(date, formatter);

            Path path = Paths.get(LOG_FILE_PATH);
            if (!Files.exists(path)) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, "File doesn't exist.");
            }

            String formattedDate = logDate.format(formatter);
            List<String> logLines = Files.readAllLines(path);
            List<String> currentLogs = logLines.stream()
                    .filter(line -> line.startsWith(formattedDate))
                    .toList();

            if (currentLogs.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, "There are no logs for specified date: " + date);
            }

            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
            Path logFile = Files.createTempFile("logs-" + logDate, ".log", attr);

            Files.write(logFile, currentLogs);

            Resource resource = new UrlResource(logFile.toUri());
            logFile.toFile().deleteOnExit();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format. Required dd-mm-yyyy");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing .log file" + e.getMessage());
        }
    }
}