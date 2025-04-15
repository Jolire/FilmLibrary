package com.cinema.filmlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Class to hold info about directors. **/
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents a director of a film.")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the director.")
    private Long id;

    @ManyToMany(mappedBy = "directors", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"directors", "reviews"})
    @Schema(description = "Films associated with the director.")
    private List<Film> films;

    @Schema(description = "Name of the director.")
    private String name;

    @Schema(description = "Nationality of the director.")
    private String nationality;

    @Schema(description = "Year of birth of the director.")
    private int birthYear;
}
