package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

@Component
@Slf4j
@Primary
@AllArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    public static final String DIRECTORS = "directors";
    public static final String FILMS_DIRECTOR = "films_director";
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM " + DIRECTORS;
        log.info("Получение списка всех режиссеров");

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director findById(Long id) {
        String sqlQuery = "SELECT * FROM " + DIRECTORS + " WHERE director_id = ?";
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
        String sqlQuery = "INSERT INTO " + DIRECTORS + " (name) VALUES (?)";

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
        log.info("Обновление режиссера с id = {}", director.getId());
        findById(director.getId());

        String sqlQuery = "UPDATE " + DIRECTORS + " SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());

        return director;
    }

    @Override
    public String delete(Long directorId) {
        log.info("Удаление режиссера с id = {}", directorId);
        String sqlQuery = "DELETE FROM " + FILMS_DIRECTOR + " WHERE director_id = ?; " +
                "DELETE FROM " + DIRECTORS + " WHERE director_id = " + directorId;

        return jdbcTemplate.update(sqlQuery, directorId) > 0 ? "Режиссер удален" : "Ошибка при удалении";
    }

    @Override
    public void addFilm(LinkedHashSet<Director> directors, Long filmId) {
        log.info("Добавление режиссеров фильму с id = {}", filmId);
        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add("INSERT INTO " + FILMS_DIRECTOR + " (film_id, director_id) VALUES");

        directors.forEach(d -> stringJoiner.add(String.format("(%s, %s),", filmId, d.getId())));

        String sqlQuery = stringJoiner.toString();

        jdbcTemplate.update(sqlQuery.substring(0, sqlQuery.length() - 1));
    }

    @Override
    public void deleteFilm(Long filmId) {
        String sqlQuery = "DELETE FROM " + FILMS_DIRECTOR + " WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public LinkedHashSet<Director>  getDirectorsByFilm(Long filmId) {
        log.info("Получение режиссеров фильма с id = {}", filmId);
        String sqlQuery = "SELECT d.* FROM " + DIRECTORS + " AS d " +
                "LEFT OUTER JOIN " + FILMS_DIRECTOR + " AS fd ON fd.director_id = d.director_id " +
            "WHERE fd.film_id = ?";

        return new LinkedHashSet<>(jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId));
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director
                .builder()
                .id(resultSet.getLong("director_id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
