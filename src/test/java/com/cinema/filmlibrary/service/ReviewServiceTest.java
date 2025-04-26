package com.cinema.filmlibrary.service;

import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.exception.ForbiddenAccessException;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.exception.ResourceNotFoundException;
import com.cinema.filmlibrary.repository.FilmRepository;
import com.cinema.filmlibrary.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private FilmService filmService;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ReviewService reviewService;

    private Film testFilm;
    private Review testReview;
    private final Long filmId = 1L;
    private final Integer reviewId = 1;

    @BeforeEach
    void setUp() {
        testFilm = new Film();
        testFilm.setId(filmId);
        testFilm.setTitle("Test Film");

        testReview = new Review();
        testReview.setId(reviewId.longValue());
        testReview.setMessage("Test review");
        testReview.setFilm(testFilm);
    }

    @Test
    void createReview_Success() {
        when(filmRepository.findById(filmId)).thenReturn(Optional.of(testFilm));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.createReview(filmId, testReview);

        assertNotNull(result);
        assertEquals(testReview.getMessage(), result.getMessage());
        assertEquals(testFilm, result.getFilm());
        verify(filmRepository).findById(filmId);
        verify(reviewRepository).save(testReview);
    }

    @Test
    void createReview_FilmNotFound() {
        when(filmRepository.findById(filmId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.createReview(filmId, testReview);
        });

        verify(filmRepository).findById(filmId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void updateReview_Success() {
        when(filmRepository.existsById(filmId)).thenReturn(true);
        when(filmService.findById(filmId)).thenReturn(testFilm);
        testFilm.setReviews(List.of(testReview));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review updatedReview = new Review();
        updatedReview.setMessage("Updated message");

        Review result = reviewService.updateReview(reviewId, updatedReview, filmId);

        assertNotNull(result);
        assertEquals("Updated message", result.getMessage());
        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository).save(testReview);
    }

    @Test
    void updateReview_ReviewIdNull() {
        assertThrows(InvalidRequestException.class, () -> {
            reviewService.updateReview(null, testReview, filmId);
        });
    }

    @Test
    void updateReview_FilmIdNull() {
        assertThrows(InvalidRequestException.class, () -> {
            reviewService.updateReview(reviewId, testReview, null);
        });
    }

    @Test
    void updateReview_FilmNotFound() {
        when(filmRepository.existsById(filmId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, testReview, filmId);
        });

        verify(filmRepository).existsById(filmId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void updateReview_ReviewNotFound() {
        when(filmRepository.existsById(filmId)).thenReturn(true);
        when(filmService.findById(filmId)).thenReturn(testFilm);
        testFilm.setReviews(List.of(testReview));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, testReview, filmId);
        });

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void updateReview_ReviewNotBelongsToFilm() {
        // Setup film with ID 1
        Film film1 = new Film();
        film1.setId(1L);

        // Setup film with ID 2
        Film film2 = new Film();
        film2.setId(2L);

        // Create review that belongs to film2
        Review reviewForFilm2 = new Review();
        reviewForFilm2.setId(reviewId.longValue());
        reviewForFilm2.setFilm(film2);

        // Setup film1 with its own reviews (empty list)
        film1.setReviews(Collections.emptyList());

        when(filmRepository.existsById(1L)).thenReturn(true);
        when(filmService.findById(1L)).thenReturn(film1);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(reviewForFilm2));

        Review updatedReview = new Review();
        updatedReview.setMessage("Updated message");

        // Try to update review that belongs to film2 using film1's ID
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateReview(reviewId, updatedReview, 1L);
        });

        verify(reviewRepository).findById(reviewId);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_Success() {
        when(reviewRepository.existsById(reviewId)).thenReturn(true);

        reviewService.deleteReview(reviewId, filmId);

        verify(reviewRepository).deleteById(reviewId);
    }

    @Test
    void deleteReview_ReviewIdNull() {
        assertThrows(InvalidRequestException.class, () -> {
            reviewService.deleteReview(null, filmId);
        });
    }

    @Test
    void deleteReview_FilmIdNull() {
        assertThrows(InvalidRequestException.class, () -> {
            reviewService.deleteReview(reviewId, null);
        });
    }

    @Test
    void deleteReview_ReviewNotFound() {
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.deleteReview(reviewId, filmId);
        });

        verify(reviewRepository).existsById(reviewId);
        verify(reviewRepository, never()).deleteById(any());
    }

    @Test
    void getReviewsByFilmId_Success() {
        when(filmRepository.existsById(filmId)).thenReturn(true);
        when(reviewRepository.findByFilmId(filmId)).thenReturn(List.of(testReview));

        List<Review> result = reviewService.getReviewsByFilmId(filmId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testReview, result.get(0));
        verify(reviewRepository).findByFilmId(filmId);
    }

    @Test
    void getReviewsByFilmId_FilmIdNull() {
        assertThrows(InvalidRequestException.class, () -> {
            reviewService.getReviewsByFilmId(null);
        });
    }

    @Test
    void getReviewsByFilmId_FilmNotFound() {
        when(filmRepository.existsById(filmId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.getReviewsByFilmId(filmId);
        });

        verify(filmRepository).existsById(filmId);
        verifyNoInteractions(reviewRepository);
    }

    @Test
    void findAllReviews_Success() {
        when(reviewRepository.findAll()).thenReturn(List.of(testReview));

        List<Review> result = reviewService.findAllReviews();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testReview, result.get(0));
        verify(reviewRepository).findAll();
    }

    @Test
    void findAllReviews_ExceptionThrown() {
        when(reviewRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(ForbiddenAccessException.class, () -> {
            reviewService.findAllReviews();
        });

        verify(reviewRepository).findAll();
    }
}