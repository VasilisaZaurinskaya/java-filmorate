package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@Primary
@AllArgsConstructor
public class MpaDbStorage implements MpaStorage {
    public static final String MPA_RATING = "mpa_rating";
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Mpa> getAllMpa() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + MPA_RATING);
        ArrayList<Mpa> mpa = new ArrayList<Mpa>();
        while (mpaRows.next()) {
            Mpa mpa1 = new Mpa();
            mpa1.setId(mpaRows.getInt(MPA_RATING + "_id"));
            mpa1.setName(mpaRows.getString("name"));
            mpa1.setDescription(mpaRows.getString("description"));
            mpa.add(mpa1);

        }
        return mpa;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM " + MPA_RATING + " WHERE mpa_rating_id = ?", id);

        if (mpaRows.next()) {

            Mpa mpa1 = new Mpa();
            mpa1.setId(mpaRows.getInt(MPA_RATING + "_id"));
            mpa1.setName(mpaRows.getString("name"));
            mpa1.setDescription(mpaRows.getString("description"));


            log.info("Найден рейтинг: {} {}", mpa1.getId(), mpa1.getName());

            return mpa1;
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            throw new NotFoundException("Рейтинг с идентификатором " + id + " не найден.");
        }
    }
}
