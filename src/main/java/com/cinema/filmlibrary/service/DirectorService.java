package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.DirectorRepository;
import com.cinema.filmlibrary.repository.FilmRepository;
import com.cinema.filmlibrary.utils.CacheUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.format.FormatterRegistrar;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Class to store business logic related to directors. */
@Service
public class DirectorService {

    private static final String ERROR_MESSAGE = "Director not found";
    private final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";

    private final DirectorRepository directorRepository;
    private final FilmService filmService;
    private final FilmRepository filmRepository;
    private final CacheUtil<Long, Director> directorCacheId;
    private final CacheUtil<Long, Film> filmCacheId;

    /** Constructor to initialize dependencies. */
    public DirectorService(DirectorRepository directorRepository, FilmService filmService,
                           FilmRepository filmRepository, CacheUtil<Long, Director> directorCacheId,
                           CacheUtil<Long, Film> filmCacheId) {
        this.directorRepository = directorRepository;
        this.filmService = filmService;
        this.filmRepository = filmRepository;
        this.directorCacheId = directorCacheId;
        this.filmCacheId = filmCacheId;
    }

    /** Finds a director by ID and verifies they worked on the specified film.
     *
     * @param id director's ID
     * @param filmId film's ID to verify association
     * @return the Director object
     * @throws EntityNotFoundException if film or director not found or not associated
     */
    public Director findById(Long id, Long filmId) {

        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");

        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,"Film not found");
        }

        Film film = filmService.findById(filmId);
        List<Director> directors = film.getDirectors();
        Director director = directorCacheId.get(id);

        if (director == null) {
            director = directorRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE));
            directorCacheId.put(id, director);
        } else {
            System.out.println("Director was got from cache");
        }

        if (directors.contains(director)) {
            return director;
        }

        throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
    }

    /** Retrieves all directors from the database.
     *
     * @return list of all directors
     */
    public List<Director> findAllDirectors() {
        try {
            return directorRepository.findAll();
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
        }
    }

    /** Saves a director and associates them with a film.
     *
     * @param director the Director object to save
     * @param filmId the ID of the film to associate with
     * @return the saved Director object
     */
    public Director save(Director director, Long filmId) {
        filmService.clearCache();
        Film film = filmService.findById(filmId);
        if (director.getName() == null || director.getName().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Name parameter cannot be empty");
        }
        if (director.getNationality() == null || director.getNationality().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Nationality parameter cannot be empty");
        }

        if (director.getBirthYear() > 2025 || director.getBirthYear() < 1925) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Birth year must be between 1925 and 2025");
        }

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

    /** Updates information about a director.
     *
     * @param id the ID of the director to update
     * @param director the updated Director object
     * @return the updated Director object
     */
    public Director update(Long id, Director director) {
        if (id == null){
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Id parameter cannot be empty");
        }

        if (director == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Director cannot be null");
        }

        if (!directorCacheId.containsKey(id)) {
            if (!directorRepository.existsById(id)) {
                throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
            }
        }

        director.setId(id);
        Director updatedDirector = directorRepository.save(director);
        directorCacheId.clear();

        for (Map.Entry<Long, Film> filmEntry : filmCacheId.entrySet()) {
            Film film = filmEntry.getValue();
            List<Director> directors = film.getDirectors();
            if (directors.removeIf(dir -> dir.getId().equals(updatedDirector.getId()))) {
                directors.add(updatedDirector);
                film.setDirectors(directors);
                filmCacheId.put(filmEntry.getKey(), film);
            }
        }

        return updatedDirector;
    }

    /** Deletes a director's association with a film.
     *
     * @param id the director's ID
     * @param filmId the film's ID to remove association from
     */
    public void delete(Long id, Long filmId) {

        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,"Film not found");
        }

        filmService.clearCache();
        Film film = filmService.findById(filmId);
        List<Director> directors = film.getDirectors();
        Director director = findById(id, filmId);

        directors.remove(director);

        film.setDirectors(directors);
        filmService.update(filmId, film);

        List<Film> films = director.getFilms();
        films.remove(film);
        if (films.isEmpty()) {
            directorRepository.delete(director);
            directorCacheId.remove(id);
        } else {
            director.setFilms(films);
            update(id, director);
        }
    }
}