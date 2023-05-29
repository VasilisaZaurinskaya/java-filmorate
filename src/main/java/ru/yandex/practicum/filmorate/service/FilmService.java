package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedService feedService;


    public Film createFilm(Film film) {
        validateNewFilm(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        validateNewFilm(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long filmId) {
        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм не может быть равен null");
            throw new NotFoundException("Фильм не может быть равен null");
        } else {
            return filmStorage.getFilmById(filmId);
        }
    }

    public void setLike(Long userId, Long filmId) {

        User user = userStorage.getUserbyId(userId);
        Film film = filmStorage.getFilmById(filmId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", userId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }
        if (film == null) {
            log.error("Не найден фильм с id = {}", filmId);
            throw new NotFoundException("Не найден фильм с указанным id");
        }

        filmStorage.addLike(filmId, userId);
        feedService.addLike(userId, filmId);
    }

    public void removeLike(Long userId, Long filmId) {

        User user = userStorage.getUserbyId(userId);
        Film film = filmStorage.getFilmById(filmId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", userId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }
        if (film == null) {
            log.error("Не найден фильм с id = {}", filmId);
            throw new NotFoundException("Не найден фильм с указанным id");
        }

        filmStorage.removeLike(filmId, userId);
        feedService.removeLike(userId, filmId);
    }

    public List<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public List<Film> findFilmsByDirector(Long id, Optional<String> sortBy) {
        if (id < 0) {
            throw new NotFoundException("id не может быть отрицательным");
        }

        return filmStorage.findFilmsByDirector(id, sortBy);
    }

    public void validateUserAndFilm(Long userId, Long filmId) {
        if (userId == null || filmId == null) {
            log.error("Фильм или пользователь не указаны");
            throw new ValidateException("Нехватка данных ");
        }
    }

    public List<Film> getSearchResults(String query, String by) {
        return filmStorage.searchBy(query, by);
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
}
