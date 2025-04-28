package com.cinema.filmlibrary.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Class that represents data transfer object of the Film. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    @NotBlank(message = "Film title cannot be blank")
    @Size(max = 100, message = "Film title must be less than 100 characters")
    private String title;

    @Min(value = 1895, message = "Release year must be after 1895")
    @Max(value = 2026, message = "Release year must be before 2100")
    private int releaseYear;

    @NotBlank(message = "Genre cannot be blank")
    @Size(max = 50, message = "Genre must be less than 50 characters")
    private String genre;

    @NotEmpty(message = "Film must have at least one director")
    private List<@Valid DirectorDto> directors;

    private List<@Valid ReviewDto> reviews;
}