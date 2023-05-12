package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbcTemplate,
            MpaDbStorage mpaDbStorage
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
    }


    @Override
    public Film createFilm(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpa_rating_id", film.getMpa().getId());


        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Long filmId = simpleJdbcInsert.executeAndReturnKey(values).longValue();

        addGenres(filmId, film.getGenres());

        return getFilmById(filmId);
    }

    private void addGenres(Long filmId, List<Genre> genres) {

        for (Genre genre:genres) {

            Map<String, Object> values = new HashMap<>();
            values.put("film_id", filmId);
            values.put("genre_id", genre.getId());

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("genre_film");
            simpleJdbcInsert.execute(values);
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()) == null)
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не найден.");
        jdbcTemplate.update(
                "update films set " +
                        " name = ?," +
                        " description = ?," +
                        " release_date = ?," +
                        " duration = ?," +
                        " mpa_rating_id = ? " +
                        "where film_id = ?",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films");
        ArrayList<Film> films = new ArrayList<Film>();
        while (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilms(film.getId()));
            film.setMpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_rating_id")));
            films.add(film);

        }
        return films;

    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", filmId);

        if (filmRows.next()) {

            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilms(filmId));
            film.setMpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_rating_id")));

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", filmId);
            return null;
        }
    }

    public List<Genre> getGenresForFilms(Long filmId) {
        String sql = "select * from genres as g left join genre_film as gf on gf.genre_id = g.genre_id where gf.film_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, filmId);
        ArrayList<Genre> genres = new ArrayList<Genre>();
        while (genreRows.next()) {
            Genre genre = new Genre();
            genre.setId(genreRows.getLong("genre_id"));
            genre.setName(genreRows.getString("name"));

            genres.add(genre);
        }
        return genres;


    }
}
