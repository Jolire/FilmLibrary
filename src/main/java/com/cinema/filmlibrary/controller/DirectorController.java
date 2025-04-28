package com.cinema.filmlibrary.controller;

import com.cinema.filmlibrary.entity.Director;
import com.cinema.filmlibrary.service.DirectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * Controller for handling director-related operations for films.
 */
@RestController
@RequestMapping("/films/{filmId}/directors")
@Tag(name = "Director requests", description = "CRUD operations for directors in films")
public class DirectorController {

    private final DirectorService directorService;

    /**
     * Constructor for DirectorController.
     *
     * @param directorService service for director operations
     */
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    /**
     * Adds a director to the specified film.
     *
     * @param director Director object to add
     * @param filmId ID of the film
     * @return created director
     */
    @Operation(summary = "Add director to film", description =
            "Creates and returns the director added to the film",
            responses = {
                @ApiResponse(responseCode = "200", description = "Director added to film"),
                @ApiResponse(responseCode = "400", description = "Invalid request",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Invalid request\" }"))),
                @ApiResponse(responseCode = "404", description = "Film not found",
                            content = @Content(schema =
                            @Schema(example = "{ \"error\": \"Film not found\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Internal server error\" }")))
            })
    @PostMapping
    public Director addDirector(
            @Parameter(description = "Director object to add to the film")
            @RequestBody Director director,
            @Parameter(description = "ID of the film") @PathVariable Long filmId) {
        return directorService.save(director, filmId);
    }

    /**
     * Updates director information.
     *
     * @param directorId ID of the director to update
     * @param director Updated director data
     * @return updated director
     */
    @Operation(summary = "Update director", description =
            "Updates the information of an existing director",
            responses = {
                @ApiResponse(responseCode = "200", description = "Director updated"),
                @ApiResponse(responseCode = "404", description = "Director not found",
                            content = @Content(schema =
                            @Schema(example = "{ \"error\": \"Director not found\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema =
                            @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @PutMapping("/{directorId}")
    public Director updateDirector(
            @Parameter(description = "ID of the director to update",
                    example = "1") @PathVariable Long directorId,
            @Parameter(description = "Updated director object")
            @RequestBody Director director) {
        return directorService.update(directorId, director);
    }

    /**
     * Removes a director from a film or deletes them if no other films exist.
     *
     * @param directorId ID of the director to remove
     * @param filmId ID of the film to remove the director from
     */
    @Operation(summary = "Remove director from film", description =
            "Removes a director from a film",
            responses = {
                @ApiResponse(responseCode = "200", description = "Director removed from film"),
                @ApiResponse(responseCode = "404", description = "Director or Film not found",
                            content = @Content(schema =
                            @Schema(example = "{ \"error\": \"Director or Film not found\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema =
                            @Schema(example = "{ \"error\": \"Internal server error\" }")))
            })
    @DeleteMapping("/{directorId}")
    public void removeDirector(
            @Parameter(description = "ID of the director to remove", example = "1")
            @PathVariable Long directorId,
            @Parameter(description = "ID of the film") @PathVariable Long filmId) {
        directorService.delete(directorId, filmId);
    }

    /**
     * Gets a director who worked on the specified film.
     *
     * @param directorId ID of the director
     * @param filmId ID of the film to verify association
     * @return director information
     */
    @Operation(summary = "Get director of a film", description =
            "Returns the director who worked on the specified film",
            responses = {
                @ApiResponse(responseCode = "200", description = "Found director for the film"),
                @ApiResponse(responseCode = "404", description = "Director or Film not found",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Director or Film not found\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Internal server error\" }")))
            })
    @GetMapping("/{directorId}")
    public Director getFilmDirector(
            @Parameter(description = "ID of the director", example = "1")
            @PathVariable Long directorId,
            @Parameter(description = "ID of the film") @PathVariable Long filmId) {
        return directorService.findById(directorId, filmId);
    }

    /**
     * Gets all directors from the database.
     *
     * @return list of all directors
     */
    @Operation(summary = "Get all directors", description =
            "Returns a list of all directors in the system",
            responses = {
                @ApiResponse(responseCode = "200", description = "List of directors returned"),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example =
                                    "{ \"error\": \"Internal server error\" }")))
            })
    @GetMapping("/all")
    public List<Director> findAllDirectors() {
        return directorService.findAllDirectors();
    }
}
