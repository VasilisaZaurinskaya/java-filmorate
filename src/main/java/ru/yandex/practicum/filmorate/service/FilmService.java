package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<Film> getFilmById(Long filmId) {
        if (filmStorage.getFilById(filmId) == null) {
            log.error("Фильм не может быть равен null");
            throw new NotFoundException("Фильм не может быть равен null");
        } else {
            return filmStorage.getFilById(filmId);
        }
    }

    public void setLike(Long userId, Long filmId) {

        Optional<User> user = userStorage.getUserbyId(userId);
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

        Optional<User> user = userStorage.getUserbyId(userId);
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

    public void validateUserAndFilm(Long userId, Long filmId) {
        if (userId == null || filmId == null) {
            log.error("Фильм или пользователь не указаны");
            throw new ValidateException("Нехватка данных ");
        }
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
