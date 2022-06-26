package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.genre.GenreStorage;

import java.util.List;
import java.util.Set;


@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GenreService {
    GenreStorage genreStorage;

    public Genre getGenre(int id) {
        return genreStorage.get(id).orElseThrow(() -> new NotFoundRequestException(
                        String.format("Genre with id %s not exist", id)
                )
        );
    }

    public Set<Genre> getFilmGenres(Long id) {
        return genreStorage.getFilmGenres(id);
    }

    public List<Genre> getGenres() {
        return genreStorage.getAll();
    }

    public void addGenres(Set<Genre> genres, Long id) {
        genreStorage.addFilmGenres(genres, id);
    }

    public void updateFilmGenres(Set<Genre> genres, Long id) {
        genreStorage.updateFilmGenres(genres, id);
    }

}
