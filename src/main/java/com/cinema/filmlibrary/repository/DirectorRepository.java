package com.cinema.filmlibrary.repository;

import com.cinema.filmlibrary.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Class that represents database containing directors. **/
@Repository
public interface DirectorRepository extends JpaRepository<Director, Long> {
    /** Function to find author bi name.
     *
     * @param name - name of the director
     * @return object of class director
     */
    Director findByName(String name);

    /** Function to check existence of object in database by name.
     *
     * @param name name pf the director
     * @return true if director exists, false otherwise
     */
    boolean existsByName(String name);
}