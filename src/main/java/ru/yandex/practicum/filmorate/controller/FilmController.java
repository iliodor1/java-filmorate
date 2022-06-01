package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadRequestException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmService.getFilm(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void putLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.putLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") int count){
        if (count <= 0) {
            throw new BadRequestException("Параметр count имеет отрицательное значение.");
        }
        return filmService.getPopular(count);
    }
}