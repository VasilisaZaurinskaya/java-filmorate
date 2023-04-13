package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.websocket.server.PathParam;
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

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public @ResponseBody Film create(@RequestBody Film film) throws ValidateException {

        filmService.validateNewFilm(film);
        filmService.createFilm(film, this);
        return film;
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film film) throws ValidateException {

        filmService.validateNewFilm(film);

        return filmService.updateFilm(film);

    }


    @GetMapping
    public @ResponseBody List<Film> getAllFilm() {
        return filmService.getAllFilms();
    }


    @PutMapping("{filmId}/like/{userId}")
    public void setLike(
            @PathVariable Long userId,
            @PathVariable Long filmId
    ) {
        filmService.validateUserAndFilm(userId, filmId);
        filmService.setLike(userId, filmId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void deleteLike(
            @PathVariable Long userId,
            @PathVariable Long filmId
    ) {
        filmService.validateUserAndFilm(userId, filmId);
        filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public @ResponseBody List<Film> getMostPopularFilms(
            @PathParam(value = "count") Long count
    ) {
        if (count == null) {
            count = 10L;
        }
        return filmService.getMostPopularFilms(count);
    }


}
