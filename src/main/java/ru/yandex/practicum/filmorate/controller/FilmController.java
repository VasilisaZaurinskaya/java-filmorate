package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

import java.util.List;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;


    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public @ResponseBody Film create(@RequestBody Film film) throws ValidateException {

        validateNewFilm(film);
        filmService.createFilm(film, this);
        return film;
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film film) throws ValidateException {

        validateNewFilm(film);

        return filmService.updateFilm(film);

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
    public @ResponseBody List<Film> getAllFilm() {
        return filmService.getAllFilms();
    }

    public void validateUserAndFilm(Long userId, Long filmId) {
        if(userId == null || filmId == null){
            log.error("Фильм и пользователь не указаны");
            throw new ValidateException("Нехватка данных ");
        }
    }

    @PutMapping
    public @ResponseBody void setLike(Long userId, Long filmId) {
        filmService.setLike(userId, filmId);
    }


}
