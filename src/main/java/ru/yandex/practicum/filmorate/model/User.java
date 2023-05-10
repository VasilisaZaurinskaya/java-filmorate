package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private String friendship_status;
    private Set<Long> friends = new HashSet<>();
    private Set<Long> likedFilms = new HashSet<>();

    public User(Long id, String email, String login, String name, LocalDate birthday,
                String friendship_status, Set<Long> friends, Set<Long> likedFilms) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendship_status = friendship_status;
        this.friends = friends;
        this.likedFilms = likedFilms;
    }

    public User() {
    }
}
