package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("genres")
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GenreController {

    GenreService genreService;

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable int id) {
        return genreService.getGenre(id);
    }

    @GetMapping
    public List<Genre> getGenres() {
        return genreService.getGenres();
    }

}
