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
    private final GenreDbStorage genreDbStorage;

    @Autowired
    public FilmDbStorage(
            JdbcTemplate jdbcTemplate,
            MpaDbStorage mpaDbStorage,
            GenreDbStorage genreDbStorage
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
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

        List<Genre> currentFilmGenres = getGenresForFilm(filmId);
        List<Long> addedGenreIds = new ArrayList<>(genres.size());
        for (Genre filmGenre :currentFilmGenres) {
            addedGenreIds.add(filmGenre.getId());
        }

        for (Genre genre : genres) {

            if (addedGenreIds.contains(genre.getId())) continue;

            Map<String, Object> values = new HashMap<>();
            values.put("film_id", filmId);
            values.put("genre_id", genre.getId());

            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("genre_film");
            simpleJdbcInsert.execute(values);

            addedGenreIds.add(genre.getId());
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

        deleteGenres(film.getId());
        addGenres(film.getId(), film.getGenres());

        return getFilmById(film.getId());
    }

    private void deleteGenres(Long filmId) {
        String sql = "delete from genre_film where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public List<Film> getAllFilms() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.*, mr.name mpa_name, mr.description mpa_description" +
                " from films as f " +
                "left join mpa_rating as mr  on mr.mpa_rating_id  = f.mpa_rating_id ");
        ArrayList<Film> films = new ArrayList<Film>();
        while (filmRows.next()) {
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilm(film.getId()));
            film.setMpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_rating_id")));
            films.add(film);

        }
        return films;

    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.*, mr.name mpa_name, mr.description mpa_description from films as f left join mpa_rating as mr  on mr.mpa_rating_id  = f.mpa_rating_id where f.film_id = ?", filmId);

        if (filmRows.next()) {

            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilm(film.getId()));
            film.setMpa(mpaDbStorage.getMpaById(filmRows.getInt("mpa_rating_id")));

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", filmId);
            return null;
        }
    }

    public List<Genre> getGenresForFilm(Long filmId) {
        String sql = "select * " +
                "from genres as g " +
                "join genre_film as gf on gf.genre_id = g.genre_id " +
                "where gf.film_id = ?";
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

    @Override
    public void addLike(Long filmId, Long userId) {

        Map<String, Object> values = new HashMap<>();
        values.put("film_id", filmId);
        values.put("user_id", userId);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("likes");
        simpleJdbcInsert.execute(values);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer limit) {

        if (limit == null) limit = 10;

        String sql = "select f.film_id " +
                "from films as f " +
                "left outer join likes as l on l.film_id = f.film_id " +
                "group by f.film_id " +
                "order by count(l.user_id) desc, f.film_id desc " +
                "limit ?";

        SqlRowSet likesRows = jdbcTemplate.queryForRowSet(sql, limit);
        ArrayList<Film> mostPopularFilms = new ArrayList<Film>();
        while (likesRows.next()) {
            Long filmId = likesRows.getLong("film_id");
            mostPopularFilms.add(getFilmById(filmId));
        }
        return mostPopularFilms;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "delete from likes where film_id = ? and user_id = ? ";
        jdbcTemplate.update(sql, filmId, userId);
    }

    public List<Map<String, Object>> getAllLikes() {
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet("select * from likes order by user_id");
        ArrayList<Map<String, Object>> likes = new ArrayList<>();
        while (likeRows.next()) {
            Map<String, Object> like = new HashMap<>();
            like.put("user_id", likeRows.getLong("user_id"));
            like.put("friend_id", likeRows.getLong("film_id"));
            likes.add(like);


        }
        return likes;
    }

    public List<Map<String, Object>> getAllGenresFilm() {
        SqlRowSet filmGenreRows = jdbcTemplate.queryForRowSet("select * from genre_film ");
        ArrayList<Map<String, Object>> filmGenres = new ArrayList<>();
        while (filmGenreRows.next()) {
            Map<String, Object> filmGenre = new HashMap<>();
            filmGenre.put("film_id", filmGenreRows.getLong("film_id"));
            filmGenre.put("genre_id", filmGenreRows.getLong("genre_id"));
            filmGenres.add(filmGenre);


        }
        return filmGenres;
    }
}