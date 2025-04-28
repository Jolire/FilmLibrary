package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.DirectorRepository;
import com.cinema.filmlibrary.repository.FilmRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Class to perform asynchronous actions with logs. */
@Service
public class DirectorService {
    private static final String ERROR_MESSAGE = "Director not found";
    private static final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";
    private static final String DIRECTORS_CACHE = "directors";
    private static final String FILMS_CACHE = "films";

    private final DirectorRepository directorRepository;
    private final FilmService filmService;
    private final FilmRepository filmRepository;

    /** The main method. */
    public DirectorService(DirectorRepository directorRepository, FilmService filmService,
                           FilmRepository filmRepository) {
        this.directorRepository = directorRepository;
        this.filmService = filmService;
        this.filmRepository = filmRepository;
    }

    /** The main method. */
    @Cacheable(value = DIRECTORS_CACHE, key = "#id")
    public Director findById(Long id, Long filmId) {
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Film not found");
        }

        Film film = filmService.findById(filmId);
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND,
                        ERROR_MESSAGE));

        if (!film.getDirectors().contains(director)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }

        return director;
    }

    /** The main method. */
    @Cacheable(DIRECTORS_CACHE)
    public List<Director> findAllDirectors() {
        try {
            return directorRepository.findAll();
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
        }
    }

    /** The main method. */
    @Transactional
    @CacheEvict(value = {DIRECTORS_CACHE, FILMS_CACHE}, allEntries = true)
    public Director save(Director director, Long filmId) {
        validateDirector(director);
        Film film = filmService.findById(filmId);

        List<Film> newFilms = new ArrayList<>();
        if (directorRepository.existsByName(director.getName())) {
            director = directorRepository.findByName(director.getName());
            newFilms = director.getFilms();
        }

        List<Director> directors = film.getDirectors();
        if (!directors.contains(director)) {
            directors.add(director);
            film.setDirectors(directors);
            newFilms.add(film);
            director.setFilms(newFilms);
        }

        return directorRepository.save(director);
    }

    /** The main method. */
    @Transactional
    @CacheEvict(value = {DIRECTORS_CACHE, FILMS_CACHE}, allEntries = true)
    public Director update(Long id, Director director) {
        if (id == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Id parameter cannot be empty");
        }
        if (director == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Director cannot be null");
        }
        if (!directorRepository.existsById(id)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }

        director.setId(id);
        return directorRepository.save(director);
    }

    /** The main method. */
    @Transactional
    @CacheEvict(value = {DIRECTORS_CACHE, FILMS_CACHE}, allEntries = true)
    public void delete(Long id, Long filmId) {
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Film not found");
        }

        Film film = filmService.findById(filmId);
        Director director = findById(id, filmId);

        List<Director> directors = film.getDirectors();
        directors.remove(director);
        film.setDirectors(directors);
        filmService.update(filmId, film);

        List<Film> films = director.getFilms();
        films.remove(film);
        if (films.isEmpty()) {
            directorRepository.delete(director);
        } else {
            director.setFilms(films);
            directorRepository.save(director);
        }
    }

    private void validateDirector(Director director) {
        if (director.getName() == null || director.getName().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Name parameter cannot be empty");
        }
        if (director.getNationality() == null || director.getNationality().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Nationality parameter cannot be empty");
        }
        if (director.getBirthYear() > 2025 || director.getBirthYear() < 1925) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Birth year must be between 1925 and 2025");
        }
    }
}