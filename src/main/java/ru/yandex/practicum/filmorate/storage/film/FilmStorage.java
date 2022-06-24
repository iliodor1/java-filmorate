package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Optional<Film> getFilm(Long id);

    void putLike(Film film, User user);

    void deleteLike(Film film, User user);

    List<Film> getPopular(int count);

}
