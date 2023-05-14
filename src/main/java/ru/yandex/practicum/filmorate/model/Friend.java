package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Friend {
    private User friend;
    private String friendshipStatus;
}
