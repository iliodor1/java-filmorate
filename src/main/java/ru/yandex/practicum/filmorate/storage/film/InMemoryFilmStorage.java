package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private long idCounter;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    private long createId() {
        return ++idCounter;
    }

    @Override
    public Film add(Film film) {
        film.setId(createId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        return Optional.of(films.get(id));
    }

    @Override
    public void putLike(Film film, User user) {
        Set<Long> likes = film.getLikes();
        likes.add(user.getId());
        film.getLikes().add(user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        film.getLikes().remove(user.getId());
    }

    @Override
    public List<Film> getPopular(int count) {
        return getFilms().stream()
                .sorted(Comparator.comparingInt(f0 -> f0.getLikes().size() * -1))
                .limit(count)
                .collect(Collectors.toList());
    }

}
