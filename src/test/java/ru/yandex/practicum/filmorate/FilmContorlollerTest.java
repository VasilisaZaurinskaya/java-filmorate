package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.service.FilmService;



import java.time.LocalDate;

public class FilmContorlollerTest {
    public static FilmService filmService;

    @Autowired
    public FilmContorlollerTest(FilmService filmService) {
        this.filmService = filmService;
    }


    private Film getDefaultFilm() {
        Film film = new Film();
        film.setName("Звёздные войны: Эпизод 1 - Скрытая угроза");
        film.setDescription("Мирная и процветающая планета Набу. Торговая федерация, не желая платить налоги, вступает в прямой конфликт с королевой Амидалой.");
        film.setDuration(136);
        film.setReleaseDate(LocalDate.of(1999, 05, 04));
        return film;
    }

    @Test
    void validateDescriptionTest() {
        FilmController filmController = new FilmController(filmService);

        Film film = getDefaultFilm();
        film.setDescription("Звёздные во́йны. Эпизо́д I: Скры́тая угро́за» (англ. Star Wars. Episode I: The Phantom Menace) — эпическая космическая опера, снятая и написанная Джорджем Лукасом. Это четвёртый фильм, выпущенный в рамках киносаги «Звёздные войны», выступающий первой частью трилогии приквелов «Звёздных войн» и начало «Саги Скайуокеров» с хронологической точки зрения. Кроме того, это четвёртая полнометражная картина Лукаса, выпущенная им после 22-летнего перерыва в режиссуре, со времён работы над своим предыдущим фильмом «Звёздные войны. Эпизод IV: Новая надежда» (1977).");

        try {
            filmController.validateNewFilm(film);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Описание не должно превышать 200 символов", e.getMessage());
        }

    }

    @Test
    void validateNameFilmTest() {
        FilmController filmController = new FilmController(filmService);

        Film film = getDefaultFilm();
        film.setName(null);

        try {
            filmController.validateNewFilm(film);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Название фильма не может быть пустым", e.getMessage());
        }

        film.setName("");
        try {
            filmController.validateNewFilm(film);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Название фильма не может быть пустым", e.getMessage());
        }

    }

    @Test
    void validateDurationTest() {
        FilmController filmController = new FilmController(filmService);

        Film film = getDefaultFilm();
        film.setDuration(-100);

        try {
            filmController.validateNewFilm(film);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }

    }

    @Test
    void validateReleaseDateTest() {
        FilmController filmController = new FilmController(filmService);

        Film film = getDefaultFilm();
        film.setReleaseDate(LocalDate.of(1894, 10, 04));
        try {
            filmController.validateNewFilm(film);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
    }
}

