package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictRequestException;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final int MAX_DESCRIPTION_SIZE = 200;
    private final LocalDate FIRST_EVER_FILM = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final UserService userService;

    @Autowired
    public FilmService(@Qualifier("dataBase") FilmStorage filmStorage,
                       UserService userService,
                       GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.genreStorage = genreStorage;
    }

    public void putLike(Long id, Long userId) {
        Film film = getFilm(id);
        User user = userService.getUser(userId);
        filmStorage.putLike(film, user);
    }

    public void deleteLike(Long id, Long userId) {
        Film film= getFilm(id);
        User user = userService.getUser(userId);
        filmStorage.deleteLike(film, user);
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    public Film addFilm(Film film) {
        isValid(film);
        if (getFilms().stream()
                .filter(x -> x.getName().equalsIgnoreCase(film.getName()))
                .anyMatch(x -> x.getReleaseDate().equals(film.getReleaseDate()))) {
            log.error(
                    "Фильм '{}' с датой релиза '{}' уже добавлен.",
                    film.getName(),
                    film.getReleaseDate()
            );
            throw new ConflictRequestException("This film already exists");
        }
        log.info("Добавлен фильм: {}", film.getName());
        return filmStorage.add(film);
    }

    public Film updateFilm(Film film) {
        isValid(film);
        getFilm(film.getId());
        log.info("Отредактирован фильм '{}'", film.getName());
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilm(Long id) {
        return filmStorage.getFilm(id).orElseThrow(() -> new NotFoundRequestException(
                String.format("Film with id '%s' does not exist", id))
        );
    }

    public Genre get(int id){
        return genreStorage.get(id).orElseThrow(()-> new NotFoundRequestException(
                        String.format("Genre with id %s not exist", id)
                )
        );
    }

    public Set<Genre> getFilmGenres(Long id){
        return genreStorage.getFilmGenres(id);
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    private void isValid(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("invalid film name");
        } else if (film.getDescription().length() > MAX_DESCRIPTION_SIZE || film.getDescription().isBlank()) {
            log.error(String.format("Максимальная длина описания — %s символов", MAX_DESCRIPTION_SIZE));
            throw new ValidationException("invalid description");
        } else if (film.getReleaseDate().isBefore(FIRST_EVER_FILM)) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("invalid release date");
        } else if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidationException("invalid duration");
        }
    }

}
