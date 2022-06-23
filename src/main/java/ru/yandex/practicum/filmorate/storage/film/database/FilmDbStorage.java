package ru.yandex.practicum.filmorate.storage.film.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("dataBase")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        String sqlQuery = "INSERT INTO films(name, description, release_date, duration, mpa_id)" +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            film.getGenres()
                    .forEach(genre -> jdbcTemplate
                            .update("INSERT INTO FILM_GENRES(film_id, GENRE_ID) VALUES (?,?);",
                            film.getId(), genre.getId()));
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films\n" +
                "SET name= ?,\n" +
                "    description = ?,\n" +
                "    release_date = ?,\n" +
                "    duration = ?,\n" +
                "    mpa_id = ?" +
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE film_id=?", film.getId());
            film.getGenres().forEach(
                    genre -> jdbcTemplate.update(
                            "INSERT INTO FILM_GENRES(film_id, GENRE_ID) VALUES (?,?);",
                            film.getId(), genre.getId()
                    )
            );
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT * FROM films LEFT JOIN MPA M on M.ID = FILMS.MPA_ID";
        return jdbcTemplate.query(sqlQuery, new FilmRowMapper());
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        String sqlQuery = "SELECT * FROM films LEFT JOIN MPA M on M.ID = FILMS.MPA_ID WHERE FILMS.ID = ?";
        Optional<Film> filmOptional = jdbcTemplate.query(sqlQuery, new FilmRowMapper(), id).stream().findFirst();
        Film film = filmOptional.orElseThrow(() -> new NotFoundRequestException(
                String.format("Film with id '%s' does not exist", id))
        );
        Set<Genre> genres = selectGenres(id);
        if (!(genres.isEmpty())) film.setGenres(genres);
        return filmOptional;
    }

    @Override
    public void putLike(Film film, User user) {
        String sqlQuery = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId(), user.getId());
    }

    @Override
    public List<Film> getPopular(int count) {
        String sqlQuery = "SELECT * " +
                "FROM films AS f " +
                "LEFT JOIN likes AS l ON f.id = l.film_id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, new FilmRowMapper(), count);
    }

    private void insertGenres(long filmId, long genreId) {
        String sqlQuery = "INSERT INTO FILM_GENRES(film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genreId);
    }

    private Set<Genre> selectGenres(long filmId) {
        String sqlQuery = "Select id, name from film_genres fg left join genres g on g.id = fg.genre_id where fg.film_id = ?;";
        return new HashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }

}
