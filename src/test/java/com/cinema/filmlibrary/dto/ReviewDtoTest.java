package com.cinema.filmlibrary.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect_thenNoViolations() {
        ReviewDto review = new ReviewDto("Great movie!", 9);
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(review);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenMessageBlank_thenOneViolation() {
        ReviewDto review = new ReviewDto("", 5);
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(review);
        assertEquals(1, violations.size());
        assertEquals("Review message cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenMessageTooLong_thenOneViolation() {
        String longMessage = "a".repeat(2001);
        ReviewDto review = new ReviewDto(longMessage, 5);
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(review);
        assertEquals(1, violations.size());
        assertEquals("Review message must be less than 2000 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenRatingTooLow_thenOneViolation() {
        ReviewDto review = new ReviewDto("Bad movie", 0);
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(review);
        assertEquals(1, violations.size());
        assertEquals("Rating must be at least 1", violations.iterator().next().getMessage());
    }

    @Test
    void whenRatingTooHigh_thenOneViolation() {
        ReviewDto review = new ReviewDto("Great movie", 11);
        Set<ConstraintViolation<ReviewDto>> violations = validator.validate(review);
        assertEquals(1, violations.size());
        assertEquals("Rating must be at most 10", violations.iterator().next().getMessage());
    }
}