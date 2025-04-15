package com.cinema.filmlibrary.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Class that represents data transfer object of the Director. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DirectorDto {
    @NotBlank(message = "Director name cannot be blank")
    @Size(max = 40, message = "Director name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Nationality cannot be blank")
    @Size(max = 30, message = "Nationality must be less than 50 characters")
    private String nationality;

    @Min(value = 1925, message = "Birth year must be after 1925")
    @Max(value = 2025, message = "Birth year must be before 2025")
    private int birthYear;
}