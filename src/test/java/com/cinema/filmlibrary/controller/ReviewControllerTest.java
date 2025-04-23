package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.dto.ReviewDto;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.mapper.ReviewMapper;
import com.cinema.filmlibrary.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @Mock
    private ReviewMapper reviewMapper;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void createReview_ShouldReturnCreatedReview() {
        Review review = new Review();
        when(reviewService.createReview(1L, review)).thenReturn(review);

        Review result = reviewController.createReview(1L, review);

        assertEquals(review, result);
        verify(reviewService).createReview(1L, review);
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview() {
        Review review = new Review();
        when(reviewService.updateReview(1, review, 1L)).thenReturn(review);

        Review result = reviewController.updateReview(1, review, 1L);

        assertEquals(review, result);
        verify(reviewService).updateReview(1, review, 1L);
    }

    @Test
    void deleteReview_ShouldCallService() {
        reviewController.deleteReview(1, 1L);
        verify(reviewService).deleteReview(1, 1L);
    }

    @Test
    void findAllReviews_ShouldReturnAllReviews() {
        Review review = new Review();
        when(reviewService.findAllReviews()).thenReturn(List.of(review));

        List<Review> result = reviewController.findAllReviews();

        assertEquals(1, result.size());
        verify(reviewService).findAllReviews();
    }

    @Test
    void getReviewsByFilmId_ShouldReturnReviewDtos() {
        Review review = new Review();
        ReviewDto reviewDto = new ReviewDto();
        when(reviewService.getReviewsByFilmId(1L)).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(reviewDto);

        List<ReviewDto> result = reviewController.getReviewsByFilmId(1L);

        assertEquals(1, result.size());
        verify(reviewService).getReviewsByFilmId(1L);
    }
}