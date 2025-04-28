package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.LogObj;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.cache.Cache;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Class to perform asynchronous actions with logs. */
@Service
public class AsyncLogService {
    private static final String LOG_FILE_PATH = "app.log";

    /** Some async method. */
    @Async("taskExecutor")
    public void createLogs(Long taskId, String date, Cache logsCache) {
        try {
            Thread.sleep(20000);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate logDate = LocalDate.parse(date, formatter);

            Path path = Paths.get(LOG_FILE_PATH);
            List<String> logLines = Files.readAllLines(path);
            String formattedDate = logDate.format(formatter);
            List<String> currentLogs = logLines.stream()
                    .filter(line -> line.startsWith(formattedDate))
                    .toList();

            if (currentLogs.isEmpty()) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,
                        "No logs for date: " + date);
            }

            // Безопасное создание временного файла с ограниченными правами
            Set<PosixFilePermission> permissions = EnumSet.of(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE
            );
            FileAttribute<Set<PosixFilePermission>> fileAttributes =
                    PosixFilePermissions.asFileAttribute(permissions);

            Path logFile = Files.createTempFile(
                    "logs-" + formattedDate,
                    ".log",
                    fileAttributes
            );

            Files.write(logFile, currentLogs);

            // Проверяем и обрабатываем результат установки прав
            if (!logFile.toFile().setReadable(true, true)) {
                throw new IOException("Failed to set read permissions for the log file");
            }
            if (!logFile.toFile().setWritable(true, true)) {
                throw new IOException("Failed to set write permissions for the log file");
            }

            logFile.toFile().deleteOnExit();

            LogObj task = new LogObj(taskId, "COMPLETED");
            task.setFilePath(logFile.toString());
            logsCache.put(taskId, task);

        } catch (IOException e) {
            LogObj task = new LogObj(taskId, "FAILED");
            task.setErrorMessage(e.getMessage());
            logsCache.put(taskId, task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}