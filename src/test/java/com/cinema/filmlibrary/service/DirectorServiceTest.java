package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.DirectorRepository;
import com.cinema.filmlibrary.repository.FilmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private FilmService filmService;

    @Mock
    private FilmRepository filmRepository;

    @InjectMocks
    private DirectorService directorService;

    private Director director;
    private Film film;

    @BeforeEach
    void setUp() {
        director = new Director();
        director.setId(1L);
        director.setName("Test Director");
        director.setNationality("Test Nationality");
        director.setBirthYear(1980);
        director.setFilms(new ArrayList<>());

        film = new Film();
        film.setId(1L);
        film.setTitle("Test Film");
        film.setDirectors(new ArrayList<>());
    }

    @Test
    void findById_Success() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        film.getDirectors().add(director);
        director.getFilms().add(film);

        Director result = directorService.findById(1L, 1L);

        assertNotNull(result);
        assertEquals(director, result);
        verify(filmRepository).existsById(1L);
        verify(filmService).findById(1L);
        verify(directorRepository).findById(1L);
    }

    @Test
    void findById_FilmIdNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.findById(1L, null));

        assertEquals("filmId cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void findById_FilmNotFound() {
        when(filmRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> directorService.findById(1L, 1L));

        assertEquals("Film not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(filmRepository).existsById(1L);
    }

    @Test
    void findById_DirectorNotFound() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> directorService.findById(1L, 1L));

        assertEquals("Director not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void findById_DirectorNotAssociatedWithFilm() {
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> directorService.findById(1L, 1L));

        assertEquals("Director not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void findAllDirectors_Success() {
        List<Director> directors = Collections.singletonList(director);
        when(directorRepository.findAll()).thenReturn(directors);

        List<Director> result = directorService.findAllDirectors();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(directorRepository).findAll();
    }

    @Test
    void findAllDirectors_ForbiddenAccess() {
        when(directorRepository.findAll()).thenThrow(new RuntimeException());

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class,
                () -> directorService.findAllDirectors());

        assertEquals("Access to this operation is forbidden", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
    }

    @Test
    void save_NewDirector_Success() {
        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.existsByName("Test Director")).thenReturn(false);
        when(directorRepository.save(any(Director.class))).thenReturn(director);

        Director result = directorService.save(director, 1L);

        assertNotNull(result);
        assertEquals(1, director.getFilms().size());
        assertEquals(1, film.getDirectors().size());
        verify(filmService).findById(1L);
        verify(directorRepository).existsByName("Test Director");
        verify(directorRepository).save(director);
    }

    @Test
    void save_ExistingDirector_Success() {
        Director existingDirector = new Director();
        existingDirector.setId(2L);
        existingDirector.setName("Test Director");
        existingDirector.setFilms(new ArrayList<>());

        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.existsByName("Test Director")).thenReturn(true);
        when(directorRepository.findByName("Test Director")).thenReturn(existingDirector);
        when(directorRepository.save(any(Director.class))).thenReturn(existingDirector);

        Director result = directorService.save(director, 1L);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(1, existingDirector.getFilms().size());
        assertEquals(1, film.getDirectors().size());
        verify(filmService).findById(1L);
        verify(directorRepository).existsByName("Test Director");
        verify(directorRepository).findByName("Test Director");
        verify(directorRepository).save(existingDirector);
    }

    @Test
    void save_DirectorAlreadyAssociatedWithFilm() {
        director.getFilms().add(film);
        film.getDirectors().add(director);

        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.existsByName("Test Director")).thenReturn(true);
        when(directorRepository.findByName("Test Director")).thenReturn(director);
        when(directorRepository.save(any(Director.class))).thenReturn(director);

        Director result = directorService.save(director, 1L);

        assertNotNull(result);
        assertEquals(1, director.getFilms().size()); // Should not add duplicate
        assertEquals(1, film.getDirectors().size()); // Should not add duplicate
    }

    @Test
    void save_ValidationFailed_NameEmpty() {
        director.setName(null);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.save(director, 1L));

        assertEquals("Name parameter cannot be empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void save_ValidationFailed_NationalityEmpty() {
        director.setNationality(null);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.save(director, 1L));

        assertEquals("Nationality parameter cannot be empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void save_ValidationFailed_BirthYearTooEarly() {
        director.setBirthYear(1924);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.save(director, 1L));

        assertEquals("Birth year must be between 1925 and 2025", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void save_ValidationFailed_BirthYearTooLate() {
        director.setBirthYear(2026);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.save(director, 1L));

        assertEquals("Birth year must be between 1925 and 2025", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void update_Success() {
        when(directorRepository.existsById(1L)).thenReturn(true);
        when(directorRepository.save(any(Director.class))).thenReturn(director);

        Director result = directorService.update(1L, director);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(directorRepository).existsById(1L);
        verify(directorRepository).save(director);
    }

    @Test
    void update_IdNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.update(null, director));

        assertEquals("Id parameter cannot be empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void update_DirectorNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.update(1L, null));

        assertEquals("Director cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void update_DirectorNotFound() {
        when(directorRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> directorService.update(1L, director));

        assertEquals("Director not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(directorRepository).existsById(1L);
    }

    @Test
    void delete_Success_DirectorRemovedFromLastFilm() {
        // Настраиваем моки для двух вызовов existsById
        when(filmRepository.existsById(1L)).thenReturn(true);
        // Настраиваем мок для двух вызовов findById (один в delete(), второй в findById())
        when(filmService.findById(1L)).thenReturn(film);
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        film.getDirectors().add(director);
        director.getFilms().add(film);

        directorService.delete(1L, 1L);

        // Проверяем, что existsById вызывался 2 раза
        verify(filmRepository, times(2)).existsById(1L);
        // Проверяем, что findById вызывался 2 раза (один в delete(), второй в findById())
        verify(filmService, times(2)).findById(1L);
        verify(directorRepository).findById(1L);
        verify(filmService).update(1L, film);
        verify(directorRepository).delete(director);
    }

    @Test
    void delete_Success_DirectorHasOtherFilms() {
        Film anotherFilm = new Film();
        anotherFilm.setId(2L);

        // Разрешаем два вызова existsById и findById
        when(filmRepository.existsById(1L)).thenReturn(true);
        when(filmService.findById(1L)).thenReturn(film).thenReturn(film); // <- второй вызов вернет тот же film
        when(directorRepository.findById(1L)).thenReturn(Optional.of(director));

        film.getDirectors().add(director);
        director.getFilms().add(film);
        director.getFilms().add(anotherFilm);

        directorService.delete(1L, 1L);

        // Проверяем, что existsById вызывался ровно 2 раза
        verify(filmRepository, times(2)).existsById(1L);

        // Проверяем, что findById вызывался 2 раза
        verify(filmService, times(2)).findById(1L);

        verify(directorRepository).findById(1L);
        verify(filmService).update(1L, film);
        verify(directorRepository).save(director);
        verify(directorRepository, never()).delete(director);
    }


    @Test
    void delete_FilmIdNull() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> directorService.delete(1L, null));

        assertEquals("filmId cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void delete_FilmNotFound() {
        when(filmRepository.existsById(1L)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> directorService.delete(1L, 1L));

        assertEquals("Film not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(filmRepository).existsById(1L);
    }
}