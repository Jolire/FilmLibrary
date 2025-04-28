package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.DirectorRepository;
import com.cinema.filmlibrary.repository.FilmRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Some code here. */
@Service
public class FilmService {
    private static final String ERROR_MESSAGE = "Film not found";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid request data";
    private static final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";
    private static final String FILMS_CACHE = "films";
    private static final String DIRECTORS_CACHE = "directors";

    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    private final FilmService self; // Self-injection for cacheable methods

    /** Some code here. */
    @Autowired
    public FilmService(FilmRepository filmRepository,
                       DirectorRepository directorRepository,
                       FilmService filmService) { // Spring will inject proxy
        this.filmRepository = filmRepository;
        this.directorRepository = directorRepository;
        this.self = filmService; // Store the proxy reference
    }

    /** Some code here. */
    @Cacheable(value = FILMS_CACHE, key = "#title")
    public Film findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Title parameter cannot be empty");
        }
        return filmRepository.findByTitle(title);
    }

    /** Some code here. */
    @Cacheable(FILMS_CACHE)
    public List<Film> findAllFilms() {
        try {
            return filmRepository.findAll();
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
        }
    }

    /** Some code here. */
    @Cacheable(value = FILMS_CACHE, key = "#id")
    public Film findById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }
        return filmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND,
                        ERROR_MESSAGE));
    }

    /** Some code here. */
    @Cacheable(value = FILMS_CACHE, key = "#directorName")
    public List<Film> findByDirectorName(String directorName) {
        if (directorName == null || directorName.trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Director name cannot be empty");
        }
        return filmRepository.findByDirectorName(directorName);
    }

    /** Some code here. */
    @Cacheable(value = FILMS_CACHE, key = "'reviewCount_' + #reviewCount")
    public List<Film> findByReviewCount(Long reviewCount) {
        if (reviewCount == null || reviewCount < 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Review count must be a positive number");
        }
        return filmRepository.findByReviewCount(reviewCount);
    }

    /** Some code here. */
    @Transactional
    @CacheEvict(value = {FILMS_CACHE, DIRECTORS_CACHE}, allEntries = true)
    public Film save(Film film) {
        validateFilm(film);

        try {
            if (film.getDirectors() != null) {
                List<Director> savedDirectors = new ArrayList<>();
                for (Director director : film.getDirectors()) {
                    if (directorRepository.existsByName(director.getName())) {
                        Director existingDirector = directorRepository
                                .findByName(director.getName());
                        savedDirectors.add(existingDirector);
                    } else {
                        savedDirectors.add(directorRepository.save(director));
                    }
                }
                film.setDirectors(savedDirectors);
            }

            if (film.getReviews() != null) {
                for (Review review : film.getReviews()) {
                    review.setFilm(film);
                }
            }

            return filmRepository.save(film);
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN,
                    "You don't have permission to create this film");
        }
    }

    /** Some code here. */
    @Transactional
    @CacheEvict(value = {FILMS_CACHE, DIRECTORS_CACHE}, key = "#id")
    public Film update(Long id, Film film) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }
        if (film == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Film object cannot be null");
        }

        // Call through self proxy to ensure caching works
        Film existingFilm = self.findById(id);
        film.setDirectors(existingFilm.getDirectors());
        film.setReviews(existingFilm.getReviews());
        film.setId(id);

        return filmRepository.save(film);
    }

    /** Some code here. */
    @Transactional
    @CacheEvict(value = {FILMS_CACHE, DIRECTORS_CACHE}, allEntries = true)
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }

        try {
            filmRepository.deleteById(id);
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN,
                    "You don't have permission to delete this film");
        }
    }

    /** Some code here. */
    @CacheEvict(value = {FILMS_CACHE, DIRECTORS_CACHE}, allEntries = true)
    public void clearCache() {
        // Spring will handle cache clearing automatically
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Film object cannot be null");
        }
        if (film.getReleaseYear() == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "ReleaseYear parameter cannot be empty");
        }
        if (film.getReleaseYear() < 1900 || film.getReleaseYear() > 2100) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "The release year has been in 1900–2100");
        }
        if (film.getTitle() == null || film.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Title parameter cannot be empty");
        }
        if (film.getGenre() == null || film.getGenre().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST,
                    "Genre parameter cannot be empty");
        }
    }
}