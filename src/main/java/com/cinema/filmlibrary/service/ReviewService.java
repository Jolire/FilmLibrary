package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.FilmRepository;
import com.cinema.filmlibrary.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Class that make CRUD operations with Review object. */
@Service
public class ReviewService {

    private static final String ERROR_MESSAGE = "Review not found";
    private static final String REVIEWS_CACHE = "reviews";
    private static final String FILMS_CACHE = "films";
    private final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";
    private final ReviewRepository reviewRepository;
    private final FilmService filmService;
    private final FilmRepository filmRepository;

    /** Constructor of the class.
     *
     * @param reviewRepository object of the ReviewRepository class
     * @param filmService object of the BookRepository class
     */
    public ReviewService(ReviewRepository reviewRepository, FilmService filmService,
                         FilmRepository filmRepository) {
        this.reviewRepository = reviewRepository;
        this.filmService = filmService;
        this.filmRepository = filmRepository;
    }

    /** Function to add review to the film.
     *
     * @param filmId id of the film
     * @param review object of the Review class
     * @return created review
     */
    @Transactional
    @CacheEvict(value = {REVIEWS_CACHE, FILMS_CACHE}, key = "#filmId")
    public Review createReview(Long filmId, Review review) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new EntityNotFoundException("Film not found"));
        review.setFilm(film);
        return reviewRepository.save(review);
    }

    /** Function to update review of the film.
     *
     * @param reviewId id of the review
     * @param review object of the Review class
     * @param filmId id of the film
     * @return updated review
     */
    @Transactional
    @CacheEvict(value = {REVIEWS_CACHE, FILMS_CACHE}, key = "#filmId")
    public Review updateReview(Integer reviewId, Review review, Long filmId) {
        if (reviewId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "ReviewId cannot be null");
        }
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,"Film not found");
        }

        Film film = filmService.findById(filmId);
        if (film == null) {
            film = filmRepository.findById(filmId).orElseThrow(() ->
                    new ResourceNotFoundException(HttpStatus.NOT_FOUND, "Film not found"));
        }

        List<Review> reviews = film.getReviews();
        Review initialReview = reviewRepository.findById(reviewId).orElseThrow(() ->
                new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE));
        for (Review r : reviews) {
            if (r.getId().equals(Long.valueOf(reviewId))) {
                initialReview.setMessage(review.getMessage());
                return reviewRepository.save(initialReview);
            }
        }

        throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
    }

    /** Function to delete review.
     *
     * @param reviewId id of the review
     */
    @Transactional
    @CacheEvict(value = {REVIEWS_CACHE, FILMS_CACHE}, key = "#filmId")
    public void deleteReview(Integer reviewId, Long filmId) {
        if (reviewId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "ReviewId cannot be null");
        }
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
        }
        reviewRepository.deleteById(reviewId);
    }

    /** Function to get all reviews of the film.
     *
     * @param filmId id of the film
     * @return reviews of the film
     */
    @Cacheable(value = REVIEWS_CACHE, key = "#filmId")
    public List<Review> getReviewsByFilmId(Long filmId) {
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,"Film not found");
        }

        return reviewRepository.findByFilmId(filmId);
    }

    /** Function to get all reviews from database.
     *
     * @return list of reviews
     */
    public List<Review> findAllReviews() {
        try {
            return reviewRepository.findAll();
        } catch (Exception e) {
            throw new ForbiddenAccessException(HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE);
        }
    }
}