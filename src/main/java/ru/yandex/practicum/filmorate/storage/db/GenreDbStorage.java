package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Primary
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    public static final String GENRES = "genres";
    private final JdbcTemplate jdbcTemplate;



    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM " + GENRES + " ORDER BY genre_id";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql);
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
    public Genre getGenreById(Long id) {
        String sql = "SELECT * FROM " + GENRES + " WHERE genre_id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, id);

        if (genreRows.next()) {

            Genre genre = new Genre();
            genre.setId(genreRows.getLong("genre_id"));
            genre.setName(genreRows.getString("name"));

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());

            return genre;
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return null;
        }

    }

    @Override
    public Long getGenreIdByName(String genreName) {
        String sql = "SELECT genre_id FROM " + GENRES + " WHERE name = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, genreName);

        if (genreRows.next()) {
            return genreRows.getLong("genre_id");
        } else {
            log.info("Жанр с именем {} не найден.", genreName);
            return null;
        }
    }

    @Override
    public Long addGenre(Genre genre) {
        String sql = "INSERT INTO " + GENRES + " (genre_id) " + "SELECT ? "
                + "WHERE NOT exists (SELECT * FROM " + GENRES + " WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Long.class, genre.getName());
    }
}
