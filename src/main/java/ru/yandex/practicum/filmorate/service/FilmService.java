package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film, FilmController filmController) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();

    }

    public Film getFilmById(Long filmId) {
        if (filmStorage.getFilById(filmId) == null) {
            log.error("Фильм не может быть равен null");
            throw new NotFoundException("Фильм не может быть равен null");
        } else {
            return filmStorage.getFilById(filmId);
        }
    }

    public void setLike(Long userId, Long filmId) {

        User user = userStorage.getUserbyId(userId);
        Film film = filmStorage.getFilById(filmId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", userId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }
        if (film == null) {
            log.error("Не найден фильм с id = {}", filmId);
            throw new NotFoundException("Не найден фильм с указанным id");
        }

        user.getLikedFilms().add(filmId);
        film.getUsersWhoLiked().add(userId);

        userStorage.updateUser(user);
        filmStorage.updateFilm(film);

    }

    public void removeLike(Long userId, Long filmId) {

        User user = userStorage.getUserbyId(userId);
        Film film = filmStorage.getFilById(filmId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", userId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }
        if (film == null) {
            log.error("Не найден фильм с id = {}", filmId);
            throw new NotFoundException("Не найден фильм с указанным id");
        }

        user.getLikedFilms().remove(filmId);
        film.getUsersWhoLiked().remove(userId);

        userStorage.updateUser(user);
        filmStorage.updateFilm(film);

    }

    public List<Film> getMostPopularFilms(Long count) {
        List<Film> topFilms = filmStorage.getAllFilms();
        topFilms.sort((o1, o2) -> o2.getUsersWhoLiked().size() - o1.getUsersWhoLiked().size());
        List<Film> result = new ArrayList<>();
        Long topFilmsSize = topFilms.size() < count
                ? topFilms.size()
                : count;
        for (int i = 0; i < topFilmsSize; i++) {
            result.add(topFilms.get(i));
        }

        return result;
    }

}
