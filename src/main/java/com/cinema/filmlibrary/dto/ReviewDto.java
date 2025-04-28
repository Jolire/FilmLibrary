package com.cinema.filmlibrary.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Class that represents data transfer object of the Review. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    @NotBlank(message = "Review message cannot be blank")
    @Size(max = 100, message = "Review message must be less than 2000 characters")
    private String message;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private int rating;
}