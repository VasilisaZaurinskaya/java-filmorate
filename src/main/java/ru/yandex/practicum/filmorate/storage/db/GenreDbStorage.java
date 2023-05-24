package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from genres order by genre_id";
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
        String sql = "select * from genres where genre_id = ?";
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
    public  Long getGenreIdByName(String genreName){
        String sql = "SELECT genre_id FROM genres WHERE name = ?";
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
        String sql = "INSERT INTO genres (genre_id) " +  "SELECT ? " +  "WHERE NOT exists (SELECT * FROM genres WHERE name = ?)";
        return jdbcTemplate.queryForObject(sql, Long.class, genre.getName());
    }
}
