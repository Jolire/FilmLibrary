package com.cinema.filmlibrary.repository;

import com.cinema.filmlibrary.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Class that represents database containing reviews. */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /** Function to get all reviews of the film.
     *
     * @param filmId id of the film
     * @return reviews of the film
     */
    List<Review> findByFilmId(Long filmId);
}