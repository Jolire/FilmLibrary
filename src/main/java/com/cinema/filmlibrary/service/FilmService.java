package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.repository.DirectorRepository;
import com.cinema.filmlibrary.repository.FilmRepository;
import com.cinema.filmlibrary.utils.CacheUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Class to store business logic of the app. */
@Service
public class FilmService {
    private final String ERROR_MESSAGE = "Film not found";
    private final String INVALID_REQUEST_MESSAGE = "Invalid request data";
    private final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";

    private final FilmRepository filmRepository;
    private final DirectorRepository directorRepository;
    private final CacheUtil<Long, Film> filmCacheId;
    private final CacheUtil<Long, List<Review>> reviewCacheId;
    private final CacheUtil<Long, Director> directorCacheId;

    /**
     * Constructor to set filmRepository variable.
     *
     * @param filmRepository объект класса filmRepository
     * */
    public FilmService(FilmRepository filmRepository, DirectorRepository directorRepository, CacheUtil<Long, Film> filmCacheId,
                       CacheUtil<Long, List<Review>> reviewCacheId, CacheUtil<Long, Director> directorCacheId) {
        this.filmRepository = filmRepository;
        this.directorRepository = directorRepository;
        this.filmCacheId = filmCacheId;
        this.reviewCacheId = reviewCacheId;
        this.directorCacheId = directorCacheId;
    }

    /** Function that returns films which contains substring "title".
     *
     * @param title название film
     * @return JSON форму объекта film
     * */
    public Film findByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Title parameter cannot be empty");
        }
        return filmRepository.findByTitle(title);
    }


    /** Function to get all films from database.
     *
     * @return all films in database
     */
    public List<Film> findAllFilms() {
        try {
            return filmRepository.findAll();
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
        }
    }

    /** Function that returns film with certain id.
     *
     * @param id number id on film
     * @return JSON form of Film object
     * */
    public Film findById(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }
        Film cacheFilm = filmCacheId.get(id);
        if (cacheFilm != null) {
            System.out.println("Film was got from cache");
            return cacheFilm;
        }
        Film film = filmRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE));
        filmCacheId.put(id, film);

        return film;
    }

    /** Function that returns films with specified director.
     *
     * @param directorName name of the director
     * @return list of films with specified director
     */
    public List<Film> findByDirectorName(String directorName) {
        if (directorName == null || directorName.trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Director name cannot be empty");
        }
        return filmRepository.findByDirectorName(directorName);
    }

    /** Function to get books with amount of reviews greater than reviewCount.
     *
     * @param reviewCount amount of reviews
     * @return list of books
     */
    public List<Film> findByReviewCount(Long reviewCount) {
        if (reviewCount == null || reviewCount < 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Review count must be a positive number");
        }
        return filmRepository.findByReviewCount(reviewCount);
    }

    /** Function that saves book in database.
     *
     * @param film object of Film
     * @return JSON form of Film
     * */
    public Film save(Film film) {
        if (film.getReleaseYear() == null){
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "ReleaseYear parameter cannot be empty");
        }
        if (film.getReleaseYear() < 1900 || film.getReleaseYear() > 2100) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "The release year has been in 1900–2100");
        }
        if (film.getTitle() == null || film.getTitle().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Title parameter cannot be empty");
        }
        if (film.getGenre() == null || film.getGenre().trim().isEmpty()) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Genre parameter cannot be empty");
        }

        if (film == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Film object cannot be null");
        }

        try {
            if (film.getDirectors() != null) {
                List<Director> savedDirectors = new ArrayList<>();

                for (Director author : film.getDirectors()) {
                    if (directorRepository.existsByName(author.getName())) {
                        Director existingAuthor = directorRepository.findByName(author.getName());
                        savedDirectors.add(existingAuthor);
                    } else {
                        savedDirectors.add(directorRepository.save(author));
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
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, "You don't have permission to create this film");
        }
    }

    /** Function that updates info about book.
     *
     * @param id идентификатор книги
     * @param book объект класса Book
     * @return JSON форму объекта Book
     * */
    public Film update(Long id, Film book) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }

        if (book == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Film object cannot be null");
        }

        if (!filmRepository.existsById(id)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }

        try {
            Film existsBook = findById(id);
            book.setDirectors(existsBook.getDirectors());
            book.setReviews(existsBook.getReviews());

            book.setId(id);

            Film updatedBook = filmRepository.save(book);
            if (filmCacheId.containsKey(id)) {
                filmCacheId.put(id, updatedBook);
                System.out.println("Film was update in cache");
            }

            return updatedBook;
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, "You don't have permission to update this film");
        }
    }

    /**
     * Function that removes book from database.
     *
     * @param id идентификатор объекта в базе данных
     * */
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "Invalid film ID");
        }

        try {
            reviewCacheId.remove(id);
            filmCacheId.remove(id);
            System.out.println("Film was delete in cache");
            directorCacheId.clear();
            filmRepository.deleteById(id);
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, "You don't have permission to delete this film");
        }
    }

    /** Function to clear book cache. */
    public void clearCache() {
        try {
            filmCacheId.clear();
            System.out.println("Film cache was cleared");
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, "You don't have permission to clear the cache");
        }
    }
}