package controller;

import exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    protected HashMap<Long, User> userStorage = new HashMap<>();
    public IdGenerator idGenerator;

    public UserController() {
    }


    @PostMapping
    public @ResponseBody User create(@RequestBody User user) throws ValidateException {
        validateNewUser(user);
        if (user.getId() == null) {
            user.setId(generateNewId());
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
