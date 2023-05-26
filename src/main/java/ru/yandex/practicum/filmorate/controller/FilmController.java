package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.websocket.server.PathParam;


import java.util.List;
import java.util.Optional;


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
        return filmService.createFilm(film);
    }

    @PutMapping
    public @ResponseBody Film update(@RequestBody Film film) throws ValidateException {
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
            @PathParam(value = "count") Integer count
    ) {
        if (count == null) {
            count = 10;
        }
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("/director/{id}")
    public @ResponseBody List<Film> getFilmsByDirector(@PathVariable Long id, @RequestParam Optional<String> sortBy) {
        return filmService.findFilmsByDirector(id, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getSearchResults(@RequestParam String query, @RequestParam String by) {
        log.info("Поступил запрос на получение результатов поиска по фильмам.");
        return filmService.getSearchResults(query, by);
    }
}
