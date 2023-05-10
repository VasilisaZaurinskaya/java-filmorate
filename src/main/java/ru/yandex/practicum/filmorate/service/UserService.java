package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUserById(Long id) {
        if (userStorage.getUserbyId(id) == null) {
            log.error("Пользватель не может быть равен null");
            throw new NotFoundException("Пользватель не может быть равен null");
        } else {
            return userStorage.getUserbyId(id);
        }
    }

    public User createUser(User user) {
        validateNewUser(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);

    }

    public User updateUser(User user) {
        validateNewUser(user);
        return userStorage.updateUser(user);
    }


    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(Long id, Long friendId) {

        User user = userStorage.getUserbyId(id);
        User userFriend = userStorage.getUserbyId(friendId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", id);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }
        if (userFriend == null) {
            log.error("Не найден пользователь с id = {}", friendId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }

        user.getFriends().add(friendId);
        userFriend.getFriends().add(id);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);

    }

    public void deleteFriend(Long id, Long friendId) {

        User user = userStorage.getUserbyId(id);
        User userFriend = userStorage.getUserbyId(friendId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", id);
            throw new ValidateException("Не найден пользователь с указанным id");
        }
        if (userFriend == null) {
            log.error("Не найден пользователь с id = {}", friendId);
            throw new ValidateException("Не найден пользователь с указанным id");
        }

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(id);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);

    }

    public List<User> getFriendList(Long userId) {
        List<User> friendList = new ArrayList<>();
        User user = userStorage.getUserbyId(userId);
        for (Long friendId : user.getFriends()) {
            User friend = userStorage.getUserbyId(friendId);
            friendList.add(friend);
        }
        return friendList;
    }

    public List<User> getMitualFriends(Long id, Long otherId) {

        List<Long> sharedFriendsIds = new ArrayList<>();
        List<User> sharedFriendsList = new ArrayList<>();

        User user = userStorage.getUserbyId(id);
        User friend = userStorage.getUserbyId(otherId);

        sharedFriendsIds.addAll(user.getFriends());
        sharedFriendsIds.retainAll(friend.getFriends());

        for (Long sharedFriendId : sharedFriendsIds) {
            sharedFriendsList.add(userStorage.getUserbyId(sharedFriendId));
        }
        return sharedFriendsList;
    }

    public void validateAddFriend(Long id, Long friendId) throws ValidateException {
        if (id == null || friendId == null) {
            log.error("Не указаны параметры для добавления в друзья");
            throw new ValidateException("id не может быть равен null");
        }
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
        if (id == null && getUserById(id) == null) {
            log.error("Не указаны параметры для удаления из друзей");
            throw new ValidateException("id не может быть равен null");
        }

    }

}
