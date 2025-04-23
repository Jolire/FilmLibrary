package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.dto.FilmDto;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.mapper.FilmMapper;
import com.cinema.filmlibrary.service.FilmService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {

    @Mock
    private FilmService filmService;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmController filmController;

    @Test
    void getFilmByTitle_ShouldReturnFilmDto() {
        Film film = new Film();
        FilmDto filmDto = new FilmDto();
        when(filmService.findByTitle("Inception")).thenReturn(film);
        when(filmMapper.toDto(film)).thenReturn(filmDto);

        FilmDto result = filmController.getFilmByTitle("Inception");

        assertEquals(filmDto, result);
        verify(filmService).findByTitle("Inception");
    }

    @Test
    void getAllFilms_ShouldReturnAllFilmDtos() {
        Film film1 = new Film();
        Film film2 = new Film();
        FilmDto filmDto1 = new FilmDto();
        FilmDto filmDto2 = new FilmDto();

        when(filmService.findAllFilms()).thenReturn(List.of(film1, film2));
        when(filmMapper.toDto(film1)).thenReturn(filmDto1);
        when(filmMapper.toDto(film2)).thenReturn(filmDto2);

        List<FilmDto> result = filmController.getAllFilms();

        assertEquals(2, result.size());
        verify(filmService).findAllFilms();
    }

    @Test
    void getFilmById_ShouldReturnFilmDto() {
        Film film = new Film();
        FilmDto filmDto = new FilmDto();
        when(filmService.findById(1L)).thenReturn(film);
        when(filmMapper.toDto(film)).thenReturn(filmDto);

        FilmDto result = filmController.getFilmById(1L);

        assertEquals(filmDto, result);
        verify(filmService).findById(1L);
    }

    @Test
    void getBooksByDirectorName_ShouldReturnFilmDtos() {
        Film film = new Film();
        FilmDto filmDto = new FilmDto();
        when(filmService.findByDirectorName("Nolan")).thenReturn(List.of(film));
        when(filmMapper.toDto(film)).thenReturn(filmDto);

        List<FilmDto> result = filmController.getBooksByDirectorName("Nolan");

        assertEquals(1, result.size());
        verify(filmService).findByDirectorName("Nolan");
    }

    @Test
    void getBooksByReviewCount_ShouldReturnFilmDtos() {
        Film film = new Film();
        FilmDto filmDto = new FilmDto();
        when(filmService.findByReviewCount(5L)).thenReturn(List.of(film));
        when(filmMapper.toDto(film)).thenReturn(filmDto);

        List<FilmDto> result = filmController.getBooksByReviewCount(5L);

        assertEquals(1, result.size());
        verify(filmService).findByReviewCount(5L);
    }

    @Test
    void createFilm_ShouldReturnCreatedFilm() {
        Film film = new Film();
        when(filmService.save(film)).thenReturn(film);

        Film result = filmController.createFilm(film);

        assertEquals(film, result);
        verify(filmService).save(film);
    }

    @Test
    void updateFilm_ShouldReturnUpdatedFilm() {
        Film film = new Film();
        when(filmService.update(1L, film)).thenReturn(film);

        Film result = filmController.updateFilm(1L, film);

        assertEquals(film, result);
        verify(filmService).update(1L, film);
    }

    @Test
    void deleteFilm_ShouldCallService() {
        filmController.deleteFilm(1L);
        verify(filmService).delete(1L);
    }

    @Test
    void createFilms_ShouldReturnCreatedFilms() {
        Film film1 = new Film();
        Film film2 = new Film();
        when(filmService.save(film1)).thenReturn(film1);
        when(filmService.save(film2)).thenReturn(film2);

        List<Film> result = filmController.createFilms(List.of(film1, film2));

        assertEquals(2, result.size());
        verify(filmService, times(2)).save(any(Film.class));
    }
}