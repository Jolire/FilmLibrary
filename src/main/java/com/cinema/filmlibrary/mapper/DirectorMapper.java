package com.cinema.filmlibrary.mapper;

import com.cinema.filmlibrary.dto.DirectorDto;
import com.cinema.filmlibrary.entity.Director;
import org.springframework.stereotype.Component;

/** Class to transform object from dto and vice versa. */
@Component
public class DirectorMapper {

    /** Function to transform standard object to DTO.
     *
     * @param director object of Director class
     * @return DTO object
     */
    public DirectorDto toDto(Director director) {
        DirectorDto directorDto = new DirectorDto();
        directorDto.setName(director.getName());
        directorDto.setBirthYear(director.getBirthYear());
        directorDto.setNationality(director.getNationality());
        return directorDto;
    }

    /** Function to transform DTO to standard object.
     *
     * @param directorDto object of AuthorDto object
     * @return standard Author object
     */
    public Director toEntity(DirectorDto directorDto) {
        Director director = new Director();
        director.setName(directorDto.getName());
        director.setBirthYear(directorDto.getBirthYear());
        director.setNationality(directorDto.getNationality());
        return director;
    }
}