package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.FilmRepository;
import com.cinema.filmlibrary.repository.ReviewRepository;
import com.cinema.filmlibrary.utils.CacheUtil;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Class that make CRUD operations with Review object. */
@Service
public class ReviewService {

    private static final String ERROR_MESSAGE = "Review not found";
    private final String FORBIDDEN_MESSAGE = "Access to this operation is forbidden";
    private final ReviewRepository reviewRepository;
    private final FilmService filmService;
    private final CacheUtil<Long, List<Review>> reviewCacheId;
    private final FilmRepository filmRepository;
    private final CacheUtil<Long, Film> filmCacheId;
            ;

    /** Constructor of the class.
     *
     * @param reviewRepository object of the ReviewRepository class
     * @param filmService object of the BookRepository class
     */
    public ReviewService(ReviewRepository reviewRepository, FilmService filmService, CacheUtil<Long, List<Review>> reviewCacheId,
                         FilmRepository filmRepository, CacheUtil<Long, Film> filmCacheId) {
        this.reviewRepository = reviewRepository;
        this.filmService = filmService;
        this.reviewCacheId = reviewCacheId;
        this.filmRepository = filmRepository;
        this.filmCacheId = filmCacheId;
    }

    /** Function to add review to the film.
     *
     * @param filmId id of the film
     * @param review object of the Review class
     * @return created review
     */
    public Review createReview(Long filmId, Review review) {
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new EntityNotFoundException("Film not found"));
        review.setFilm(film);

        Film cachedFilm = filmCacheId.get(filmId);
        if (cachedFilm != null) {
            List<Review> reviews = cachedFilm.getReviews();
            reviews.add(review);
            cachedFilm.setReviews(reviews);
            filmCacheId.put(filmId, cachedFilm);
        }

        reviewCacheId.clear();

        return reviewRepository.save(review);
    }

    /** Function to update review of the film.
     *
     * @param reviewId id of the review
     * @param review object of the Review class
     * @param filmId id of the film
     * @return updated review
     */
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
                Review updatedReview = reviewRepository.save(initialReview);

                List<Review> cachedReviews = reviewCacheId.get(filmId);
                if (cachedReviews != null) {
                    cachedReviews.removeIf(rev -> rev.getId().equals(Long.valueOf(reviewId)));
                    cachedReviews.add(updatedReview);
                    reviewCacheId.put(filmId, cachedReviews);
                }

                Film cachedFilm = filmCacheId.get(filmId);
                if (cachedFilm != null) {
                    if (cachedReviews == null) {
                        cachedReviews = cachedFilm.getReviews();
                        cachedReviews.removeIf(rev -> rev.getId().equals(Long.valueOf(reviewId)));
                        cachedReviews.add(updatedReview);
                    }
                    cachedFilm.setReviews(cachedReviews);
                    filmCacheId.put(filmId, cachedFilm);
                }

                return updatedReview;
            }
        }

        throw new ResourceNotFoundException(HttpStatus.NOT_FOUND, ERROR_MESSAGE);
    }

    /** Function to delete review.
     *
     * @param reviewId id of the review
     */
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
        List<Review> reviews = reviewCacheId.get(filmId);
        if (reviews != null) {
            reviews.removeIf(rev -> rev.getId().equals(Long.valueOf(reviewId)));
            reviewCacheId.put(filmId, reviews);
        }

        Film film = filmCacheId.get(filmId);
        if (film != null) {
            if (reviews == null) {
                reviews = film.getReviews();
                reviews.removeIf(rev -> rev.getId().equals(Long.valueOf(reviewId)));
            }
            film.setReviews(reviews);
            filmCacheId.put(filmId, film);
        }
    }

    /** Function to get all reviews of the film.
     *
     * @param filmId id of the film
     * @return reviews of the film
     */
    public List<Review> getReviewsByFilmId(Long filmId) {
        if (filmId == null) {
            throw new InvalidRequestException(HttpStatus.BAD_REQUEST, "filmId cannot be null");
        }
        if (!filmRepository.existsById(filmId)) {
            throw new ResourceNotFoundException(HttpStatus.NOT_FOUND,"Film not found");
        }

        List<Review> reviews = reviewCacheId.get(filmId);
        if (reviews != null) {
            System.out.println("Reviews was got from cache");
            return reviews;
        }

        reviews = reviewRepository.findByFilmId(filmId);
        reviewCacheId.put(filmId, reviews);

        return reviews;
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