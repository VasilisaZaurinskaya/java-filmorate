package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    User user;
    private String friendshipStatus;

}
