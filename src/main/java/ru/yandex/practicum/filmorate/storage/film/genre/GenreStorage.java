package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Optional<Genre> get(int id);

    Set<Genre> getFilmGenres(Long id);

    List<Genre> getAll();

    void addFilmGenres(Set<Genre> genres, Long id);

    void updateFilmGenres(Set<Genre> genres, Long id);
}
