package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Primary
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM directors";
        log.info("Получение списка всех режиссеров");

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(Long id) {
        String sqlQuery = "SELECT * FROM directors WHERE id = ?";
        log.info("Получение списка режиссера с id = {}", id);

        try {
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссер не найден");
        }
    }

    @Override
    public Director create(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlQuery = "INSERT INTO directors (name) VALUES (?)";

        log.info("Создание режиссера: {}", director);

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());

            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return Director
                .builder()
                .id(id)
                .name(director.getName())
                .build();
    }

    @Override
    public Director update(Director director) {
        findById(director.getId());

        String sqlQuery = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return director;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director
                .builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
