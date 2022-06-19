package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
@Qualifier("dataBase")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration)\n" +
                "VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films\n" +
                "SET name= ?,\n" +
                "    description = ?,\n" +
                "    release_date = ?,\n" +
                "    duration = ?\n" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getId());
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(Long id) {
        String sqlQuery = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        Integer duration = resultSet.getInt("duration");

        return new Film(id, name, description, releaseDate, duration);
    }

}
