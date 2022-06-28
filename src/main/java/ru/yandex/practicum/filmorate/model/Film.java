package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Mpa mpa;

    @JsonIgnore
    private final Set<Long> likes = new HashSet<>();
    private Set<Genre> genres;

    public Film(Long id,
                String name,
                String description,
                LocalDate releaseDate,
                Integer duration,
                Mpa mpa,
                Set<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

}
