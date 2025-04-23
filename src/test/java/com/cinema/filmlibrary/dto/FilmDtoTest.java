package com.cinema.filmlibrary.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrect_thenNoViolations() {
        DirectorDto director = new DirectorDto("Director", "British", 1989);
        ReviewDto review = new ReviewDto("Great movie!", 9);

        FilmDto film = new FilmDto(
                "Inception",
                2010,
                "Sci-Fi",
                List.of(director),
                List.of(review)
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenTitleBlank_thenOneViolation() {
        FilmDto film = new FilmDto(
                "",
                2010,
                "Sci-Fi",
                List.of(new DirectorDto("Director", "British", 1989)),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Film title cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenTitleTooLong_thenOneViolation() {
        String longTitle = "a".repeat(101);
        FilmDto film = new FilmDto(
                longTitle,
                2010,
                "Sci-Fi",
                List.of(new DirectorDto()),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Film title must be less than 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenReleaseYearTooEarly_thenOneViolation() {
        FilmDto film = new FilmDto(
                "Title",
                1894,
                "Sci-Fi",
                List.of(new DirectorDto("Director", "British", 1989)),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Release year must be after 1895", violations.iterator().next().getMessage());
    }

    @Test
    void whenReleaseYearTooLate_thenOneViolation() {
        FilmDto film = new FilmDto(
                "Title",
                2027,
                "Sci-Fi",
                List.of(new DirectorDto("Director", "British", 1989)),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Release year must be before 2100", violations.iterator().next().getMessage());
    }

    @Test
    void whenGenreBlank_thenOneViolation() {
        FilmDto film = new FilmDto(
                "Title",
                2010,
                "",
                List.of(new DirectorDto("Director", "British", 1989)),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Genre cannot be blank", violations.iterator().next().getMessage());
    }

    @Test
    void whenGenreTooLong_thenOneViolation() {
        String longGenre = "a".repeat(51);
        FilmDto film = new FilmDto(
                "Title",
                2010,
                longGenre,
                List.of(new DirectorDto("Director", "British", 1989)),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Genre must be less than 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenDirectorsEmpty_thenOneViolation() {
        FilmDto film = new FilmDto(
                "Title",
                2010,
                "Sci-Fi",
                Collections.emptyList(),
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Film must have at least one director", violations.iterator().next().getMessage());
    }

    @Test
    void whenDirectorsNull_thenOneViolation() {
        FilmDto film = new FilmDto(
                "Title",
                2010,
                "Sci-Fi",
                null,
                null
        );

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Film must have at least one director", violations.iterator().next().getMessage());
    }
}