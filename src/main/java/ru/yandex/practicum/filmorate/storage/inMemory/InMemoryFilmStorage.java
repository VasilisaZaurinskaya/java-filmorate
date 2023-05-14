package ru.yandex.practicum.filmorate.storage.inMemory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    protected HashMap<Long, Film> filmStorage = new HashMap<>();


    @Override
    public Film createFilm(Film film) {
        film.setId(generateNewId());
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
        } else if (!filmStorage.containsKey(film.getId())) {
            log.error("Фильм с id = {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        } else {
            filmStorage.put(film.getId(), film);
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Film getFilmById(Long filmId) {
        return filmStorage.get(filmId);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        throw new UnsupportedOperationException();
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
