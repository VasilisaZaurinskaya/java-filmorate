package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    protected HashMap<Long, Film> filmStorage = new HashMap<>();

    public void createFilm(Film film, FilmController filmController) {
        film.setId(generateNewId());
        filmStorage.put(film.getId(), film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null || !filmStorage.containsKey(film.getId())) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
        } else {
            filmStorage.put(film.getId(), film);
        }
        return film;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());

    }

    public long getMaxId() {
        long maxId = 0;
        for (long id : filmStorage.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    public long generateNewId() {
        long maxId = getMaxId();
        long newId = maxId + 1;
        return newId;
    }
}
