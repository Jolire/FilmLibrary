package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.service.DirectorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorControllerTest {

    @Mock
    private DirectorService directorService;

    @InjectMocks
    private DirectorController directorController;

    @Test
    void addDirector_ShouldReturnCreatedDirector() {
        Director director = new Director();
        when(directorService.save(director, 1L)).thenReturn(director);

        Director result = directorController.addDirector(director, 1L);

        assertEquals(director, result);
        verify(directorService).save(director, 1L);
    }

    @Test
    void updateDirector_ShouldReturnUpdatedDirector() {
        Director director = new Director();
        when(directorService.update(1L, director)).thenReturn(director);

        Director result = directorController.updateDirector(1L, director);

        assertEquals(director, result);
        verify(directorService).update(1L, director);
    }

    @Test
    void removeDirector_ShouldCallService() {
        directorController.removeDirector(1L, 1L);
        verify(directorService).delete(1L, 1L);
    }

    @Test
    void getFilmDirector_ShouldReturnDirector() {
        Director director = new Director();
        when(directorService.findById(1L, 1L)).thenReturn(director);

        Director result = directorController.getFilmDirector(1L, 1L);

        assertEquals(director, result);
        verify(directorService).findById(1L, 1L);
    }

    @Test
    void findAllDirectors_ShouldReturnAllDirectors() {
        List<Director> directors = List.of(
                new Director(),
                new Director()
        );
        when(directorService.findAllDirectors()).thenReturn(directors);

        List<Director> result = directorController.findAllDirectors();

        assertEquals(2, result.size());
        verify(directorService).findAllDirectors();
    }
}