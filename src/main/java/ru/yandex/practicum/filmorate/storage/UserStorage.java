package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserbyId(Long id);

    List<User> getFriendList(Long userId);

    List<User> getMitualFriends(Long id, Long otherId);

    void createFriend(User user, User friend);

    void deleteFriend(Long id, Long friendId);
}
