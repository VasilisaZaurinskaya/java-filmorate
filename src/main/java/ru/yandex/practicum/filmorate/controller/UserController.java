package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    protected HashMap<Long, User> userStorage = new HashMap<>();

    public UserController() {
    }


    @PostMapping
    public @ResponseBody User create(@RequestBody User user) throws ValidateException {
        validateNewUser(user);
        user.setId(generateNewId());
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public @ResponseBody User update(@RequestBody User user) throws ValidateException {
        validateNewUser(user);
        if (user.getId() == null || !userStorage.containsKey(user.getId())) {
            throw new ValidateException("id  не может быть null");
        } else {
            userStorage.put(user.getId(), user);
        }
        return user;
    }


    public void validateNewUser(User user) throws ValidateException {
        if (user.getName() == null && user.getLogin() == null)
            throw new ValidateException("Имя пользователя и логин являются пустыми");
        if (user.getLogin() == null || user.getLogin().contains(" "))
            throw new ValidateException("Неправильный логин пользователя!");
        if (user.getEmail() == null || !user.getEmail().contains("@"))
            throw new ValidateException("Неправильный email пользователя");
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidateException("Неправильная дата рождения пользователя");

    }

    @GetMapping
    public @ResponseBody ArrayList<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    private long getMaxId() {
        long maxId = 0;
        for (long id : userStorage.keySet()) {
            if (id > maxId) {
                maxId = id;
            }
        }
        return maxId;
    }

    public long generateNewId() {
        long maxId = getMaxId();
        long newId = maxId + 1;
        return newId;
    }
}
