package com.cinema.filmlibrary.mapper;

import com.cinema.filmlibrary.dto.DirectorDto;
import com.cinema.filmlibrary.dto.FilmDto;
import com.cinema.filmlibrary.dto.ReviewDto;
import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.entity.Review;
import java.util.List;
import org.springframework.stereotype.Component;

/** Class to transform object from dto and vice versa. */
@Component
public class FilmMapper {
    private final DirectorMapper directorMapper;
    private final ReviewMapper reviewMapper;

    /** Constructor of the class. */
    public FilmMapper(DirectorMapper directorMapper, ReviewMapper reviewMapper) {
        this.directorMapper = directorMapper;
        this.reviewMapper = reviewMapper;
    }

    /** Function to transform standard object to DTO.
     *
     * @param film object of the Book class
     * @return dto object
     */
    public FilmDto toDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setTitle(film.getTitle());
        filmDto.setGenre(film.getGenre());
        filmDto.setReleaseYear(film.getReleaseYear());
        if (film.getDirectors() != null) {
            List<DirectorDto> directorDtos = film.getDirectors().stream()
                    .map(directorMapper::toDto)
                    .toList();
            filmDto.setDirectors(directorDtos);
        }

        if (film.getReviews() != null) {
            List<ReviewDto> reviewsDtos = film.getReviews().stream()
                    .map(reviewMapper::toDto)
                    .toList();
            filmDto.setReviews(reviewsDtos);
        }

        return filmDto;
    }

    /** Function to transform DTO to standard object.
     *
     * @param filmDto object of the BookDto class
     * @return standard Book object
     */
    public Film toEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setTitle(filmDto.getTitle());
        film.setGenre(filmDto.getGenre());
        film.setReleaseYear(filmDto.getReleaseYear());
        if (filmDto.getDirectors() != null) {
            List<Director> directors = filmDto.getDirectors().stream()
                    .map(directorMapper::toEntity)
                    .toList();
            film.setDirectors(directors);
        }

        if (filmDto.getReviews() != null) {
            List<Review> reviews = filmDto.getReviews().stream()
                    .map(reviewMapper::toEntity)
                    .toList();
            film.setReviews(reviews);
        }
        return film;
    }
}