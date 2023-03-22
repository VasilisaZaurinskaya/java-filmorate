package ru.yandex.practicum.filmorate;

import controller.FilmController;
import controller.UserController;
import exception.ValidateException;
import model.Film;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {

    @Test
    void validateEmailTest() {
        UserController userController = new UserController();

        User user = getDefaultUser();
        user.setEmail("NOT_A_EMAIL");

        try {
            userController.validateNewUser(user);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный email пользователя", e.getMessage());
        }

        user.setEmail(null);

        try {
            userController.validateNewUser(user);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный email пользователя", e.getMessage());
        }
    }

    @Test
    void validateNameTest() {
        UserController userController = new UserController();

        User user = getDefaultUser();
        user.setName(null);
        user.setLogin(null);

        try {
            userController.validateNewUser(user);
        } catch (ValidateException e) {
            Assertions.assertEquals("Имя пользователя и логин являются пустыми", e.getMessage());
        }

    }

    @Test
    void validateLoginTest() {
        UserController userController = new UserController();

        User user = getDefaultUser();
        user.setLogin(null);

        try {
            userController.validateNewUser(user);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный логин пользователя!", e.getMessage());
        }

    }

    @Test
    void validateBirthdayTest() {
        UserController userController = new UserController();

        User user = getDefaultUser();
        user.setBirthday(LocalDate.now().plusMonths(1));
        try {
            userController.validateNewUser(user);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильная дата рождения пользователя", e.getMessage());
        }
    }

    @Test
    void validateDescriptionTest() {
        FilmController filmController = new FilmController();

        Film film = getDefaultFilm();
        film.setDescription("Звёздные во́йны. Эпизо́д I: Скры́тая угро́за» (англ. Star Wars. Episode I: The Phantom Menace) — эпическая космическая опера, снятая и написанная Джорджем Лукасом. Это четвёртый фильм, выпущенный в рамках киносаги «Звёздные войны», выступающий первой частью трилогии приквелов «Звёздных войн» и начало «Саги Скайуокеров» с хронологической точки зрения. Кроме того, это четвёртая полнометражная картина Лукаса, выпущенная им после 22-летнего перерыва в режиссуре, со времён работы над своим предыдущим фильмом «Звёздные войны. Эпизод IV: Новая надежда» (1977).");

        try {
            filmController.validateNewFilm(film);
        } catch (ValidateException e) {
            Assertions.assertEquals("Описание не должно превышать 200 символов", e.getMessage());
        }

    }

    @Test
    void validateNameFilmTest() {
        FilmController filmController = new FilmController();

        Film film = getDefaultFilm();
        film.setName(null);


        try {
            filmController.validateNewFilm(film);
        } catch (ValidateException e) {
            Assertions.assertEquals("Название фильма не может быть пустым", e.getMessage());
        }

    }

    @Test
    void validateDurationTest() {
        FilmController filmController = new FilmController();

        Film film = getDefaultFilm();
        film.setDuration(Duration.ofMinutes(-100));

        try {
            filmController.validateNewFilm(film);
        } catch (ValidateException e) {
            Assertions.assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());
        }

    }

    @Test
    void validateReleaseDateTest() {
        FilmController filmController = new FilmController();

        Film film = getDefaultFilm();
        film.setReleaseDate(LocalDate.of(1894, 10, 04));
        try {
            filmController.validateNewFilm(film);
        } catch (ValidateException e) {
            Assertions.assertEquals("Дата релиза не может быть раньше 28 декабря 1895 года", e.getMessage());
        }
    }

    private User getDefaultUser() {
        User user = new User();
        user.setName("NAME");
        user.setLogin("LOGIN");
        user.setEmail("email@example.ru");
        user.setBirthday(LocalDate.of(2002, 04, 29));
        return user;
    }

    private Film getDefaultFilm() {
        Film film = new Film();
        film.setName("Звёздные войны: Эпизод 1 - Скрытая угроза");
        film.setDescription("Мирная и процветающая планета Набу. Торговая федерация, не желая платить налоги, вступает в прямой конфликт с королевой Амидалой.");
        film.setDuration(Duration.ofMinutes(136));
        film.setReleaseDate(LocalDate.of(1999, 05, 04));
        return film;
    }
}
