package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void putLike(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            log.error("Фильм с id '{}' не найден в списке!", id);
            throw new FilmNotFoundException(String.format("Фильм с id '%d' не найден.", id));
        }
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);
    }

    public void deleteLike(Long id, Long userId) {
        Film film = filmStorage.getFilm(id);
        if (film == null) {
            log.error("Фильм с id '{}' не найден в списке!", id);
            throw new FilmNotFoundException(String.format("Фильм с id '%d' не найден.", id));
        }
        if (!(film.getLikes().contains(userId))) {
            log.error("Пользователь с id '{}' не найден.", userId);
            throw new FilmNotFoundException(
                    String.format("Пользователь с id '%d' не найден.", userId)
            );
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id);
    }
}
