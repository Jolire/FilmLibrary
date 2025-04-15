package com.cinema.filmlibrary.repository;

import com.cinema.filmlibrary.entity.Film;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** Class that represents database containing books. */
@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    /** Function that returns book with concrete title.
     *
     * @param title название книги
     * @return JSON форму объекта Film
     * */
    Film findByTitle(String title);

    /** Function to find book by id.
     *
     * @param id id of the film
     * @return object of Film class
     */
    @EntityGraph(value = "Film", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Film> findById(Long id);

    /** Function with custom query to get films by director name.
     *
     * @param directorName name of the director
     * @return list of films with specified director
     */
    @Query(value = "SELECT f.* FROM film f JOIN film_director fd ON f.id = fd.film_id "
            + "JOIN director d ON fd.director_id = d.id WHERE d.name = :directorName",
            nativeQuery = true)
    List<Film> findByDirectorName(@Param("directorName") String directorName);

    /** Function with custom query to get films with amount of reviews greater than reviewCount.
     *
     * @param reviewCount amount of reviews
     * @return list of films
     */
    @Query("SELECT film FROM Film film JOIN film.reviews review GROUP BY"
            + " film HAVING COUNT(review) > :reviewCount")
    List<Film> findByReviewCount(@Param("reviewCount") Long reviewCount);
}