package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.dto.ReviewDto;
import com.cinema.filmlibrary.entity.Review;
import com.cinema.filmlibrary.mapper.ReviewMapper;
import com.cinema.filmlibrary.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling review operations for films.
 */
@RestController
@RequestMapping("/films/{filmId}/reviews")
@Tag(name = "Review requests", description = "CRUD operations for reviews of films")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    /** Constructor for ReviewController.
     *
     * @param reviewService service for review operations
     * @param reviewMapper mapper for converting between Review and ReviewDto
     */
    public ReviewController(ReviewService reviewService, ReviewMapper reviewMapper) {
        this.reviewService = reviewService;
        this.reviewMapper = reviewMapper;
    }

    /** Adds a review to the specified film.
     *
     * @param filmId ID of the film
     * @param review Review object to add
     * @return created Review
     */
    @Operation(summary = "Add a review to a film", description =
            "Adds a new review for the specified film",
            responses = {
                @ApiResponse(responseCode = "201", description =
                        "Review created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Invalid input data\" }")))
            })
    @PostMapping
    public Review createReview(@PathVariable Long filmId, @RequestBody Review review) {
        return reviewService.createReview(filmId, review);
    }

    /** Updates an existing review for a film.
     *
     * @param reviewId ID of the review to update
     * @param review Updated review data
     * @param filmId ID of the associated film
     * @return updated Review
     */
    @Operation(summary = "Update a review", description =
            "Updates an existing review for the specified film",
            responses = {
                @ApiResponse(responseCode = "200", description =
                        "Review updated successfully"),
                @ApiResponse(responseCode = "404", description = "Review not found",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Review not found\" }"))),
                @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Invalid input data\" }")))
            })
    @PutMapping("/{reviewId}")
    public Review updateReview(
            @PathVariable int reviewId,
            @RequestBody Review review,
            @PathVariable Long filmId) {
        return reviewService.updateReview(reviewId, review, filmId);
    }

    /** Deletes a review.
     *
     * @param reviewId ID of the review to delete
     * @param filmId ID of the associated film
     */
    @Operation(summary = "Delete a review", description = "Deletes a review for the specified film",
            responses = {
                @ApiResponse(responseCode = "200", description = "Review deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Review not found",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Review not found\" }")))
            })
    @DeleteMapping("/{reviewId}")
    public void deleteReview(
            @PathVariable int reviewId,
            @PathVariable Long filmId) {
        reviewService.deleteReview(reviewId, filmId);
    }

    /** Gets all reviews from the database.
     *
     * @return list of all reviews
     */
    @Operation(summary = "Get all reviews", description =
            "Retrieves a list of all reviews in the system",
            responses = {
                @ApiResponse(responseCode = "200", description = "List of all reviews"),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Internal server error\" }")))
            })
    @GetMapping("/all")
    public List<Review> findAllReviews() {
        return reviewService.findAllReviews();
    }

    /** Gets all reviews for a specific film.
     *
     * @param filmId ID of the film
     * @return list of ReviewDtos for the film
     */
    @Operation(summary = "Get reviews by film ID", description =
            "Retrieves reviews for the specified film",
            responses = {
                @ApiResponse(responseCode = "200", description = "List of reviews for the film"),
                @ApiResponse(responseCode = "404", description = "Film not found",
                            content = @Content(schema
                                    = @Schema(example = "{ \"error\": \"Film not found\" }")))
            })
    @GetMapping
    public List<ReviewDto> getReviewsByFilmId(@PathVariable Long filmId) {
        List<Review> reviews = reviewService.getReviewsByFilmId(filmId);
        return reviews.stream()
                .map(reviewMapper::toDto)
                .toList();
    }
}
