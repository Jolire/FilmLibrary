package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
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
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private DirectorRepository directorRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private FilmService filmService;

    private Film testFilm;
    private Director testDirector;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testDirector = new Director();
        testDirector.setId(1L);
        testDirector.setName("Test Director");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setMessage("Great film!");
        testReview.setRating(5);

        testFilm = new Film();
        testFilm.setId(1L);
        testFilm.setTitle("Test Film");
        testFilm.setGenre("Drama");
        testFilm.setReleaseYear(2020);
        testFilm.setDirectors(List.of(testDirector));
        testFilm.setReviews(List.of(testReview));
    }

    @Test
    void findByTitle_ValidTitle_ReturnsFilm() {
        when(filmRepository.findByTitle("Test Film")).thenReturn(testFilm);

        Film result = filmService.findByTitle("Test Film");

        assertNotNull(result);
        assertEquals("Test Film", result.getTitle());
        verify(filmRepository).findByTitle("Test Film");
    }

    @Test
    void findByTitle_EmptyTitle_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.findByTitle(""));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title parameter cannot be empty", exception.getMessage());
    }

    @Test
    void findByTitle_NullTitle_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.findByTitle(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title parameter cannot be empty", exception.getMessage());
    }

    @Test
    void findAllFilms_Success_ReturnsFilmsList() {
        when(filmRepository.findAll()).thenReturn(List.of(testFilm));

        List<Film> result = filmService.findAllFilms();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(filmRepository).findAll();
    }

    @Test
    void findAllFilms_RepositoryException_ThrowsForbiddenAccessException() {
        when(filmRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class,
                () -> filmService.findAllFilms());

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("Access to this operation is forbidden", exception.getMessage());
    }

    @Test
    void findById_ValidId_ReturnsFilm() {
        when(filmRepository.findById(1L)).thenReturn(Optional.of(testFilm));

        Film result = filmService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(filmRepository).findById(1L);
    }

    @Test
    void findById_InvalidId_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.findById(0L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid film ID", exception.getMessage());
    }

    @Test
    void findById_NotFound_ThrowsResourceNotFoundException() {
        when(filmRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> filmService.findById(1L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Film not found", exception.getMessage());
    }

    @Test
    void findByDirectorName_ValidName_ReturnsFilms() {
        when(filmRepository.findByDirectorName("Test Director")).thenReturn(List.of(testFilm));

        List<Film> result = filmService.findByDirectorName("Test Director");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(filmRepository).findByDirectorName("Test Director");
    }

    @Test
    void findByDirectorName_EmptyName_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.findByDirectorName(""));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Director name cannot be empty", exception.getMessage());
    }

    @Test
    void findByReviewCount_ValidCount_ReturnsFilms() {
        when(filmRepository.findByReviewCount(1L)).thenReturn(List.of(testFilm));

        List<Film> result = filmService.findByReviewCount(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(filmRepository).findByReviewCount(1L);
    }

    @Test
    void findByReviewCount_NegativeCount_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.findByReviewCount(-1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Review count must be a positive number", exception.getMessage());
    }

    @Test
    void save_ValidFilm_ReturnsSavedFilm() {
        when(directorRepository.existsByName("Test Director")).thenReturn(true);
        when(directorRepository.findByName("Test Director")).thenReturn(testDirector);
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);

        Film result = filmService.save(testFilm);

        assertNotNull(result);
        assertEquals("Test Film", result.getTitle());
        verify(filmRepository).save(testFilm);
    }

    @Test
    void save_NewDirector_SavesDirector() {
        Director newDirector = new Director();
        newDirector.setName("New Director");
        testFilm.setDirectors(List.of(newDirector));

        when(directorRepository.existsByName("New Director")).thenReturn(false);
        when(directorRepository.save(newDirector)).thenReturn(newDirector);
        when(filmRepository.save(any(Film.class))).thenReturn(testFilm);

        Film result = filmService.save(testFilm);

        assertNotNull(result);
        verify(directorRepository).save(newDirector);
    }

    @Test
    void save_NullFilm_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Film object cannot be null", exception.getMessage());
    }

    @Test
    void save_InvalidReleaseYear_ThrowsInvalidRequestException() {
        testFilm.setReleaseYear(1800);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("The release year has been in 1900–2100", exception.getMessage());
    }

    @Test
    void save_EmptyTitle_ThrowsInvalidRequestException() {
        testFilm.setTitle("");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title parameter cannot be empty", exception.getMessage());
    }

    @Test
    void save_EmptyGenre_ThrowsInvalidRequestException() {
        testFilm.setGenre("");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Genre parameter cannot be empty", exception.getMessage());
    }

    @Test
    void save_RepositoryException_ThrowsForbiddenAccessException() {
        when(filmRepository.save(any(Film.class))).thenThrow(new RuntimeException("Database error"));

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You don't have permission to create this film", exception.getMessage());
    }

    @Test
    void update_ValidFilm_ReturnsUpdatedFilm() {
        Film originalFilm = new Film();
        originalFilm.setId(1L);
        originalFilm.setTitle("Original Film");
        originalFilm.setGenre("Drama");
        originalFilm.setReleaseYear(2020);

        when(filmRepository.findById(1L)).thenReturn(Optional.of(originalFilm));

        Film updatedFilm = new Film();
        updatedFilm.setTitle("Updated Film");
        updatedFilm.setGenre("Comedy");
        updatedFilm.setReleaseYear(2021);

        // Мок save() возвращает обновлённый фильм
        when(filmRepository.save(any(Film.class))).thenReturn(updatedFilm);

        Film result = filmService.update(1L, updatedFilm);

        assertNotNull(result);
        assertEquals("Updated Film", result.getTitle());  // Теперь должно проходить
        assertEquals(1L, result.getId());
        verify(filmRepository).save(any(Film.class));
    }

    @Test
    void update_InvalidId_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.update(0L, testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid film ID", exception.getMessage());
    }

    @Test
    void update_NullFilm_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.update(1L, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Film object cannot be null", exception.getMessage());
    }

    @Test
    void delete_ValidId_DeletesFilm() {
        doNothing().when(filmRepository).deleteById(1L);

        filmService.delete(1L);

        verify(filmRepository).deleteById(1L);
    }

    @Test
    void delete_InvalidId_ThrowsInvalidRequestException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.delete(0L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid film ID", exception.getMessage());
    }

    @Test
    void delete_RepositoryException_ThrowsForbiddenAccessException() {
        // Если в сервисе используется existsById() перед удалением
        doThrow(new RuntimeException("Database error")).when(filmRepository).deleteById(1L);

        ForbiddenAccessException exception = assertThrows(ForbiddenAccessException.class,
                () -> filmService.delete(1L));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
        assertEquals("You don't have permission to delete this film", exception.getMessage());
    }

    @Test
    void clearCache_DoesNotThrowExceptions() {
        assertDoesNotThrow(() -> filmService.clearCache());
    }

    @Test
    void validateFilm_NullFilm_ThrowsException() {
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Film object cannot be null", exception.getMessage());
    }

    @Test
    void validateFilm_NullReleaseYear_ThrowsException() {
        testFilm.setReleaseYear(null);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("ReleaseYear parameter cannot be empty", exception.getMessage());
    }

    @Test
    void validateFilm_InvalidReleaseYear_ThrowsException() {
        testFilm.setReleaseYear(3000);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("The release year has been in 1900–2100", exception.getMessage());
    }

    @Test
    void validateFilm_EmptyTitle_ThrowsException() {
        testFilm.setTitle("");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Title parameter cannot be empty", exception.getMessage());
    }

    @Test
    void validateFilm_EmptyGenre_ThrowsException() {
        testFilm.setGenre("");

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> filmService.save(testFilm));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Genre parameter cannot be empty", exception.getMessage());
    }
}