package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public @ResponseBody User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public @ResponseBody User create(@RequestBody User user) throws ValidateException {
        validateNewUser(user);
        return userService.createUser(user);
    }


    @PutMapping
    public @ResponseBody User update(@RequestBody User user) throws ValidateException {
        validateNewUser(user);
        return userService.updateUser(user);
    }

    @GetMapping
    public @ResponseBody List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    public void validateNewUser(User user) throws ValidateException {

        if (user.getName() == null && user.getLogin() == null) {
            log.error("Имя пользователя и логин являются пустыми");
            throw new ValidateException("Имя пользователя и логин являются пустыми");
        }
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Неправильный логин пользователя!");
            throw new ValidateException("Неправильный логин пользователя!");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.error("Неправильный email пользователя");
            throw new ValidateException("Неправильный email пользователя");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Неправильная дата рождения пользователя");
            throw new ValidateException("Неправильная дата рождения пользователя");
        }


    }

    public void validateUserFriend(Long id) {
        if (id == null && userService.getUserById(id) == null) {
            log.error("Не указаны параметры для удаления из друзей");
            throw new ValidateException("id не может быть равен null");
        }

    }

    public void validateAddFriend(Long id, Long friendId) throws ValidateException {
        if (id == null || friendId == null) {
            log.error("Не указаны параметры для добавления в друзья");
            throw new ValidateException("id не может быть равен null");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        validateAddFriend(id, friendId);
        userService.addFriend(id, friendId);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        validateUserFriend(id);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{userId}/friends")
    public @ResponseBody List<User> getFriendList(
            @PathVariable Long userId
    ) {
        return userService.getFriendList(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public @ResponseBody List<User> getMitualFriends(
            @PathVariable Long id,
            @PathVariable Long otherId
    ) {
        return userService.getMitualFriends(id, otherId);
    }
}
