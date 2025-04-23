package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    @Test
    void downloadLogs_ShouldReturnResponseEntity() {
        ResponseEntity<Resource> expectedResponse = ResponseEntity.ok().build();
        when(logService.downloadLogs("19-03-2025")).thenReturn(expectedResponse);

        ResponseEntity<Resource> result = logController.downloadLogs("19-03-2025");

        assertEquals(expectedResponse, result);
        verify(logService).downloadLogs("19-03-2025");
    }
}