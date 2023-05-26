package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


@Component
@Slf4j
@Primary
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final DirectorDbStorage directorDbStorage;
    private final MpaDbStorage mpaDbStorage;


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

        if (film.getDirectors().size() != 0) {
            directorDbStorage.addFilm(film.getDirectors(), filmId);
        }

        return getFilmById(filmId);
    }

    private void addGenres(Long filmId, List<Genre> genres) {

        List<Genre> currentFilmGenres = getGenresForFilm(filmId);
        List<Long> addedGenreIds = new ArrayList<>(genres.size());

        for (Genre filmGenre : currentFilmGenres) {
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
        if (getFilmById(film.getId()) == null) {
            throw new NotFoundException("Фильм с идентификатором " + film.getId() + " не найден.");
        }

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

        if (film.getDirectors().size() == 0) {
            directorDbStorage.deleteFilm(film.getId());
        } else {
            directorDbStorage.addFilm(film.getDirectors(), film.getId());
        }

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
                "join mpa_rating as mr  on mr.mpa_rating_id  = f.mpa_rating_id ");
        ArrayList<Film> films = new ArrayList<>();
        while (filmRows.next()) {
            Mpa mpa = new Mpa();
            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilm(film.getId()));
            film.setDirectors(directorDbStorage.getDirectorsByFilm(film.getId()));
            mpa.setId(filmRows.getInt("mpa_rating_id"));
            mpa.setName(filmRows.getString("MPA_NAME"));
            mpa.setDescription(filmRows.getString("MPA_DESCRIPTION"));
            film.setMpa(mpa);
            films.add(film);

        }

        return films;
    }

    @Override
    public Film getFilmById(Long filmId) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select f.*, mr.name mpa_name, mr.description mpa_description " +
                "from films as f" +
                "  join mpa_rating as mr " +
                " on mr.mpa_rating_id  = f.mpa_rating_id where f.film_id = ?", filmId);
        Mpa mpa = new Mpa();
        if (filmRows.next()) {

            Film film = new Film();
            film.setId(filmRows.getLong("film_id"));
            film.setName(filmRows.getString("name"));
            film.setDescription(filmRows.getString("description"));
            Date releaseDateField = filmRows.getDate("release_date");
            film.setReleaseDate(releaseDateField != null ? releaseDateField.toLocalDate() : null);
            film.setDuration(filmRows.getInt("duration"));
            film.setGenres(getGenresForFilm(film.getId()));
            mpa.setId(filmRows.getInt("mpa_rating_id"));
            mpa.setName(filmRows.getString("MPA_NAME"));
            mpa.setDescription(filmRows.getString("MPA_DESCRIPTION"));
            film.setDirectors(directorDbStorage.getDirectorsByFilm(filmId));
            film.setMpa(mpa);

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
        ArrayList<Genre> genres = new ArrayList<>();
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
        ArrayList<Film> mostPopularFilms = new ArrayList<>();
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

    @Override
    public List<Film> findFilmsByDirector(Long directorId, Optional<String> sortBy) {
        log.info("Получение фильмов режиссера с id = {}", directorId);

        StringJoiner sqlQuery = new StringJoiner(" ");

        sqlQuery.add(
                "SELECT f.*, count(l.user_id) AS likes, EXTRACT(YEAR FROM (f.release_date)) AS sort_by_year " +
                        "FROM films AS f " +
                        "LEFT OUTER JOIN films_director AS fd ON f.film_id = fd.film_id " +
                        "LEFT OUTER JOIN likes AS l ON l.film_id = f.film_id " +
                        "WHERE fd.director_id = " + directorId + " " +
                        "GROUP BY f.film_id"
        );

        switch (sortBy.orElse("")) {
            case "like": {
                sqlQuery.add("ORDER BY sort_by_like ASC");
                break;
            }
            case "year": {
                sqlQuery.add("ORDER BY sort_by_year ASC");
                break;
            }
        }

        List<Film> films = mapToFilms(
                jdbcTemplate.queryForList(sqlQuery.toString())
        );

        if (films.size() == 0) {
            throw new NotFoundException("Фильма режиссера не найдены");
        }

        return films;
    }

    private List<Film> mapToFilms(List<Map<String, Object>> allFilms) {
        ArrayList<Film> films = new ArrayList<>();

        allFilms.forEach(f -> {
            Long id = Long.parseLong(f.get("film_id").toString());

            films.add(
                    Film
                            .builder()
                            .id(id)
                            .description(f.get("description").toString())
                            .name(f.get("name").toString())
                            .duration((int) f.get("duration"))
                            .releaseDate(LocalDate.parse(f.get("release_date").toString()))
                            .directors(directorDbStorage.getDirectorsByFilm(id))
                            .mpa(mpaDbStorage.getMpaById((int) f.get("mpa_rating_id")))
                            .genres(getGenresForFilm(id))
                            .build()
            );
        });

        return films;
    }

    @Override
    public List<Film> searchBy(String query, String by) {
        List<Film> searchResults = new ArrayList<>();

        switch (by) {
            case "title": {
                String sql = "SELECT films.*, G.genre_id, GT.name AS genre_name, COUNT(l.film_id) as count " +
                        "FROM films " +
                        "LEFT JOIN genre_film g ON films.film_id = g.film_id " +
                        "LEFT JOIN genres gt on g.genre_id = gt.genre_id " +
                        "LEFT JOIN likes l ON films.film_id=l.film_id " +
                        "WHERE LOWER(films.name) LIKE LOWER(CONCAT('%',?,'%')) " +
                        "GROUP BY films.film_id " +
                        "ORDER BY count DESC";
                searchResults = jdbcTemplate.query(sql, this::mapToFilm, query);
                break;
            }
            case "director": {
                String sql = "SELECT films.*, COUNT(l.film_id) as count " +
                        "FROM films " +
                        "JOIN films_director  df ON films.film_id=df.film_id " +
                        "JOIN directors  d ON df.director_id=d.director_id " +
                        "LEFT JOIN likes l ON films.film_id=l.film_id " +
                        "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%',?,'%')) " +
                        "GROUP BY films.film_id " +
                        "ORDER BY count DESC";
                searchResults = jdbcTemplate.query(sql, this::mapToFilm, query);
                break;
            }
            case "title,director": {
                String sql = "SELECT films.*, COUNT(l.film_id) as count " +
                        "FROM films " +
                        "LEFT JOIN films_director  df ON films.film_id=df.film_id " +
                        "LEFT JOIN directors  d ON df.director_id=d.director_id " +
                        "LEFT JOIN likes l ON films.film_id=l.film_id " +
                        "WHERE LOWER(films.name) LIKE LOWER(CONCAT('%',?,'%')) " +
                        "OR LOWER(d.name) LIKE LOWER(CONCAT('%',?,'%')) " +
                        "GROUP BY films.film_id " +
                        "ORDER BY count DESC";
                searchResults = jdbcTemplate.query(sql, this::mapToFilm, query, query);
                break;
            }
        }
        return searchResults;
    }

    private Film mapToFilm(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Integer duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Mpa mpa = mpaDbStorage.getMpaById(rs.getInt("mpa_rating_id"));
        List<Genre> genres = getGenresForFilm(id);
        LinkedHashSet<Director> directors = directorDbStorage.getDirectorsByFilm(id);

        log.info("Создание объекта фильма из базы с id {}", id);

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .duration(duration)
                .releaseDate(releaseDate)
                .mpa(mpa)
                .genres(genres)
                .directors(directors)
                .build();
    }
}