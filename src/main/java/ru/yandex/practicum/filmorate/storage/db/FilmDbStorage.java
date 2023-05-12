package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Film createFilm(Film film) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "insert into film(" +
                        "name, " +
                        "description, " +
                        "releaseDate, " +
                        "duration," +
                        "genre," +
                        "mpa_rating," +
                        "usersWhoLiked) " +
                        "VALUES ( ?,?,? ,?, ?,?,?)",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenre()
        );
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(
                "update film set(" +
                        "name, " +
                        "description, " +
                        "releaseDate, " +
                        "duration," +
                        "genre," +
                        "mpa_rating," +
                        "usersWhoLiked) " +
                        "VALUES ( ?,?,? ,?, ?,?,?) where id=?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getGenre(),
                film.getMpa_rating(),
                film.getUsersWhoLiked()
        );
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select id, name, description, releaseDate, duration, genre, mpa_rating, usersWhoLiked from film");
        ArrayList<Film> films = new ArrayList<Film>();
        while (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("releaseDate").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setGenre(filmRows.getString("genre"));
            film.setMpa_rating(filmRows.getString("mpa_rating"));
            films.add(film);

        }
        return films;
    }

    @Override
    public Film getFilById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select* from film where id = ?", filmId);

        if (filmRows.next()) {

            Film film = new Film();
            film.setId(filmRows.getLong("id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            film.setReleaseDate(filmRows.getDate("releaseDate").toLocalDate());
            film.setDuration(filmRows.getInt("duration"));
            film.setGenre(filmRows.getString("genre"));
            film.setMpa_rating(filmRows.getString("mpa_rating"));

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", filmId);
            return null;
        }
    }
}
