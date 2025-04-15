package com.cinema.filmlibrary.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Class that represents film. */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@NamedEntityGraph(
        name = "Film",
        attributeNodes = {
                @NamedAttributeNode("directors"),
                @NamedAttributeNode("reviews")
        }
)
@Schema(description = "Represents a film.")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the film.")
    private Long id;

    @Schema(description = "Title of the film.")
    private String title;

    @Schema(description = "Genre of the film.")
    private String genre;

    @Column(name = "release_year")
    @Schema(description = "Release year of the film.")
    private Integer releaseYear;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "film_director",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    @Schema(description = "Directors associated with the film.")
    private List<Director> directors;

    @OneToMany(mappedBy = "film", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Schema(description = "Reviews associated with the film.")
    private List<Review> reviews;
}
