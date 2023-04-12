package ru.yandex.practicum.filmorate;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.service.UserService;
import java.time.LocalDate;

@SpringBootTest
class UserControllerTest {

    private final UserService userService;

    @Autowired
    public UserControllerTest(UserService userService) {
        this.userService = userService;

    }

    @Test
    void validateEmailTest() {
        UserController userController = new UserController(userService);

        User user = getDefaultUser();
        user.setEmail("NOT_A_EMAIL");


        try {
            userController.validateNewUser(user);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный email пользователя", e.getMessage());
        }

        user.setEmail(null);

        try {
            userController.validateNewUser(user);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный email пользователя", e.getMessage());
        }
    }

    @Test
    void validateNameTest() {
        UserController userController = new UserController(userService);

        User user = getDefaultUser();
        user.setName(null);
        user.setLogin(null);

        try {
            userController.validateNewUser(user);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Имя пользователя и логин являются пустыми", e.getMessage());
        }

    }

    @Test
    void validateLoginTest() {
        UserController userController = new UserController(userService);

        User user = getDefaultUser();
        user.setLogin(null);

        try {
            userController.validateNewUser(user);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильный логин пользователя!", e.getMessage());
        }

    }

    @Test
    void validateBirthdayTest() {
        UserController userController = new UserController(userService);

        User user = getDefaultUser();
        user.setBirthday(LocalDate.now().plusMonths(1));
        try {
            userController.validateNewUser(user);
            Assertions.assertEquals(true, false);
        } catch (ValidateException e) {
            Assertions.assertEquals("Неправильная дата рождения пользователя", e.getMessage());
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

}
