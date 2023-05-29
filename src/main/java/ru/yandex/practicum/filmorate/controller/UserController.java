package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final FeedService feedService;


    @GetMapping("/{id}")
    public @ResponseBody User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public @ResponseBody User create(@RequestBody User user) throws ValidateException {
        return userService.createUser(user);
    }


    @PutMapping
    public @ResponseBody User update(@RequestBody User user) throws ValidateException {
        return userService.updateUser(user);
    }

    @GetMapping
    public @ResponseBody List<User> getAllUsers() {
        return userService.getAllUsers();
    }


    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userService.validateAddFriend(id, friendId);
        userService.addFriend(id, friendId);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(
            @PathVariable Long id,
            @PathVariable Long friendId
    ) {
        userService.validateUserFriend(id);
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

    @GetMapping("/{userId}/feed")
    public @ResponseBody List<Feed> getFeed(@PathVariable Long userId) {
        User user = userService.getUserById(userId);

        if (user == null) {
            log.error("Не найден пользователь с id = {}", userId);
            throw new NotFoundException("Не найден пользователь с указанным id");
        }

        return feedService.getFeedByUserId(userId);
    }


}
