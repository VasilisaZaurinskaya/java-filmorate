package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    protected HashMap<Long, Film> filmStorage = new HashMap<>();


    public FilmController() {
    }

    @PostMapping
    public @ResponseBody Film create(@RequestBody Film film) throws ValidateException {


        film.setId(generateNewId());
        if (film.getName() == null) {
            log.error("Название фильма не может быть пустым");
            throw new ValidateException("Название фильма не может быть пустым");
        }
        filmStorage.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film film) throws ValidateException {


        if (film.getId() == null || !filmStorage.containsKey(film.getId())) {
            log.error("id  не может быть null");
            throw new ValidateException("id  не может быть null");
        } else {
            filmStorage.put(film.getId(), film);
        }
        return film;

    }

    public void validateNewFilm(Film film) throws ValidateException {

        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidateException("Название фильма не может быть пустым");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDescription().length() > 200) {
            log.error("Описание не должно превышать 200 символов");
            throw new ValidateException("Описание не должно превышать 200 символов");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма должна быть положительной");
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }

    }

    @GetMapping
    public @ResponseBody ArrayList<Film> getAllFilm() {
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
