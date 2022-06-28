package ru.yandex.practicum.filmorate.storage.film.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaRowMapper;

import java.util.List;
import java.util.Optional;

@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        String sqlQuery = "SELECT * FROM mpa";
        return jdbcTemplate.query(sqlQuery, new MpaRowMapper());
    }

    @Override
    public Optional<Mpa> get(int id) {
        String sqlQuery = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.query(sqlQuery, new MpaRowMapper(), id).stream().findFirst();
    }

}
