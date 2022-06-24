package ru.yandex.practicum.filmorate.storage.film.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.GenreRowMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Genre> get(int id) {
        String sqlQuery = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper(), id).stream().findFirst();
    }

    @Override
    public Set<Genre> getFilmGenres(Long id){
        String sqlQuery = "SELECT id," +
                "       name\n" +
                "FROM film_genres fg\n" +
                "LEFT JOIN genres g ON g.id = fg.genre_id\n" +
                "WHERE fg.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, new GenreRowMapper(), id));
    }

    @Override
    public List<Genre> getAll(){
        String sqlQuery = "SELECT * FROM genres";
        return jdbcTemplate.query(sqlQuery, new GenreRowMapper());
    }

    @Override
    public void addFilmGenres(Set<Genre> genres, Long id) {
        genres.forEach(genre -> jdbcTemplate
                .update("INSERT INTO film_genres(film_id, genre_id)\n" +
                                "VALUES (?,?)",
                        id, genre.getId()));
    }

    @Override
    public void updateFilmGenres(Set<Genre> genres, Long id) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", id);
        genres.forEach(
                genre -> jdbcTemplate.update(
                        "INSERT INTO film_genres(film_id, genre_id) VALUES (?,?);",
                        id, genre.getId()
                )
        );
    }

}
