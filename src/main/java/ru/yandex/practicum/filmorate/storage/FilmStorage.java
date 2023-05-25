package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;


public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(Long filmId);

    void addLike(Long filmId, Long userId);

    List<Film> getMostPopularFilms(Integer count);

    void removeLike(Long filmId, Long userId);

    List<Film> findFilmsByDirector(Long id, Optional<String> sortBy);


}
