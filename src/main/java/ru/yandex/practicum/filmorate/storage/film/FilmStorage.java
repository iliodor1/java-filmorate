package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film add(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();

    Film getFilm(Long id);
}
