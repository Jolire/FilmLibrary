package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.dto.FilmDto;
import com.cinema.filmlibrary.entity.Film;
import com.cinema.filmlibrary.exception.InvalidRequestException;
import com.cinema.filmlibrary.mapper.FilmMapper;
import com.cinema.filmlibrary.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controller for handling film-related operations. */
@RestController
@RequestMapping("/films")
@Tag(name = "Film requests", description = "CRUD operations for films in the cinema library")
public class FilmController {

    private final FilmService filmService;
    private final FilmMapper filmMapper;

    /** Constructor for FilmController.
     *
     * @param filmService service for film operations
     * @param filmMapper mapper for converting between Film and FilmDto
     */
    public FilmController(FilmService filmService, FilmMapper filmMapper) {
        this.filmService = filmService;
        this.filmMapper = filmMapper;
    }

    /** Gets films by title containing substring.
     *
     * @param title substring to search in film titles
     * @return FilmDto of the found film
     */
    @Operation(summary = "Get films by title", description = "Searches for films whose titles contain the specified substring",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Films found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Invalid request\" }")))
            })
    @GetMapping
    public FilmDto getFilmByTitle(@RequestParam(required = false) String title) {
        Film film = filmService.findByTitle(title);
        return filmMapper.toDto(film);
    }

    /** Gets all films from database.
     *
     * @return list of all FilmDtos
     */
    @Operation(summary = "Get all films", description = "Returns a list of all films in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of all films"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @GetMapping("/all")
    public List<FilmDto> getAllFilms() {
        List<Film> films = filmService.findAllFilms();
        return films.stream()
                .map(filmMapper::toDto)
                .toList();
    }

    /** Gets film by ID.
     *
     * @param id ID of the film
     * @return FilmDto of the requested film
     */
    @Operation(summary = "Get film by ID", description = "Returns a film by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Film found"),
                    @ApiResponse(responseCode = "404", description = "Film not found",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Film not found\" }"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        Film film = filmService.findById(id);
        return filmMapper.toDto(film);
    }

    /** Gets films by director's name.
     *
     * @param directorName name of the director
     * @return list of FilmDtos by specified director
     */
    @Operation(summary = "Get films by director's name", description = "Searches for films by the director's name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Films by director found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Invalid request\" }")))
            })
    @GetMapping("/find")
    public List<FilmDto> getBooksByDirectorName(@RequestParam(required = false) String directorName) {
        return filmService.findByDirectorName(directorName).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    /** Function to get films with review amount greater than reviewCount.
     *
     * @param reviewCount amount of reviews
     * @return list of films
     */
    @Operation(summary = "Get films by review count", description = "Searches for films with review counts greater than the specified value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Films with sufficient reviews found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Invalid request\" }")))
            })
    @GetMapping("/find/reviews")
    public List<FilmDto> getBooksByReviewCount(@RequestParam(required = false) Long reviewCount) {
        return filmService.findByReviewCount(reviewCount).stream()
                .map(filmMapper::toDto)
                .toList();
    }

    /** Creates a new film.
     *
     * @param film Film object to create
     * @return created Film object
     */
    @Operation(summary = "Создать фильм")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Фильм создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(schema = @Schema(example = "{ \"error\": \"Invalid request data\" }"))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен",
                    content = @Content(schema = @Schema(example = "{ \"error\": \"Access forbidden\" }")))
    })
    @PostMapping
    @ResponseStatus(code = org.springframework.http.HttpStatus.CREATED)
    public Film createFilm(@RequestBody Film film) {
        return filmService.save(film);
    }

    /** Function to save some films for one request.
     *
     * @param films list of films
     * @return list of the books in JSON format
     */
    @PostMapping("/bulk")
    public List<Film> createFilms(@Valid @RequestBody List<Film> films) {
        return films.stream().map(filmService::save).toList();
    }

    /** Updates an existing film.
     *
     * @param id ID of the film to update
     * @param film updated Film data
     * @return updated Film object
     */
    @Operation(summary = "Update a film", description = "Updates the details of an existing film",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Film updated"),
                    @ApiResponse(responseCode = "404", description = "Film not found",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Film not found\" }"))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Invalid request\" }"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @PutMapping("/{id}")
    public Film updateFilm(@PathVariable Long id, @RequestBody Film film) {
        return filmService.update(id, film);
    }

    /** Deletes a film.
     *
     * @param id ID of the film to delete
     */
    @Operation(summary = "Delete a film", description = "Deletes a film by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Film deleted"),
                    @ApiResponse(responseCode = "404", description = "Film not found",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Film not found\" }"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.delete(id);
    }
}
