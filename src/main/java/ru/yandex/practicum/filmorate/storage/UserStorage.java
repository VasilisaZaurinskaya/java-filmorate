package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    void createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserbyId(Long id);
}
