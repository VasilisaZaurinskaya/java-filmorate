package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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
    public List<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false, name = "count") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year
    ) {
        return filmService.getMostPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
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

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteFilm(id);

    }
}
